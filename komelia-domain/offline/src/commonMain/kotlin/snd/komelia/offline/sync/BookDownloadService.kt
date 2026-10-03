package snd.komelia.offline.sync

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.PlatformFile
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.io.Sink
import kotlinx.io.files.Path
import snd.komelia.offline.book.actions.BookKomgaImportAction
import snd.komelia.offline.library.actions.LibraryKomgaImportAction
import snd.komelia.offline.series.actions.SeriesKomgaImportAction
import snd.komelia.offline.server.actions.MediaServerSaveAction
import snd.komelia.offline.sync.model.DownloadEvent
import snd.komelia.offline.sync.model.DownloadEvent.BookDownloadProgress
import snd.komelia.offline.user.actions.UserKomgaImportAction
import snd.komga.client.book.KomgaBook
import snd.komga.client.book.KomgaBookClient
import snd.komga.client.book.KomgaBookId
import snd.komga.client.library.KomgaLibrary
import snd.komga.client.library.KomgaLibraryClient
import snd.komga.client.series.KomgaSeries
import snd.komga.client.series.KomgaSeriesClient
import snd.komga.client.user.KomgaUserClient


private val logger = KotlinLogging.logger { }
private const val DEFAULT_BUFFER_SIZE: Int = 64 * 1024
internal const val DOWNLOAD_MAX_ATTEMPTS = 3
private const val DOWNLOAD_RETRY_DELAY_MILLIS = 750L

class BookDownloadService(
    private val libraryDownloadPath: Flow<PlatformFile>,

    private val bookClient: KomgaBookClient,
    private val seriesClient: KomgaSeriesClient,
    private val libraryClient: KomgaLibraryClient,
    private val userClient: KomgaUserClient,

    private val saveUserAction: UserKomgaImportAction,
    private val saveServerAction: MediaServerSaveAction,
    private val libraryImportAction: LibraryKomgaImportAction,
    private val seriesImportAction: SeriesKomgaImportAction,
    private val bookImportAction: BookKomgaImportAction,

    private val onlineServerUrl: StateFlow<String>,
) {

    fun downloadBook(bookId: KomgaBookId): Flow<DownloadEvent> {
        return flow {
            var file: PlatformFile? = null
            val book = bookClient.getOne(bookId)
            try {
                val user = userClient.getMe()
                val serverUrl = onlineServerUrl.value
                val library = libraryClient.getLibrary(book.libraryId)
                val series = seriesClient.getOneSeries(book.seriesId)

                val bookFile = doDownload(library, series, book).also { file = it }

                val offlineServer = saveServerAction.execute(serverUrl)
                libraryImportAction.execute(library, offlineServer.id)
                seriesImportAction.execute(series)
                val offlineUser = saveUserAction.execute(user, offlineServer.id)
                bookImportAction.execute(
                    book = book,
                    offlinePath = bookFile,
                    userId = offlineUser.id,
                    localFileModifiedDate = book.fileLastModified
                )
            } catch (e: Exception) {
                file?.let { deleteFile(it) }
                currentCoroutineContext().ensureActive()
                logger.catching(e)
                emit(
                    DownloadEvent.BookDownloadError(
                        bookId = bookId,
                        error = e,
                        book = book
                    )
                )
            }
        }
    }

    private suspend fun FlowCollector<DownloadEvent>.doDownload(
        library: KomgaLibrary,
        series: KomgaSeries,
        book: KomgaBook,
    ): PlatformFile {
        val url = URLBuilder(onlineServerUrl.value).build()
        return retryTransientDownload(
            onRetry = { attempt, error ->
                logger.warn(error) {
                    "Book ${book.id.value} download attempt $attempt failed; retrying"
                }
            }
        ) {
            val (file, output) = prepareOutput(
                downloadRoot = libraryDownloadPath.first(),
                serverName = buildString {
                    append(url.host)
                    if (url.specifiedPort != 0) append("_${url.specifiedPort}")
                    url.segments.forEach { append("_$it") }
                },
                libraryName = library.name,
                seriesName = series.name,
                bookFileName = Path(book.url).name,
            )

            try {
                bookClient.getBookFile(book.id) { response ->
                    val length = response.headers["Content-Length"]?.toLong() ?: 0L
                    emit(BookDownloadProgress(book, length, 0))
                    val channel = response.bodyAsChannel().counted()

                    while (!channel.isClosedForRead) {
                        output.writePacket(channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong()))
                        emit(BookDownloadProgress(book, length, channel.totalBytesRead))
                    }
                }
                output.close()
            } catch (error: Exception) {
                runCatching { output.close() }
                runCatching { deleteFile(file) }.onFailure(error::addSuppressed)
                throw error
            }

            emit(DownloadEvent.BookDownloadCompleted(book))
            file
        }
    }
}

internal suspend fun <T> retryTransientDownload(
    maxAttempts: Int = DOWNLOAD_MAX_ATTEMPTS,
    retryDelayMillis: Long = DOWNLOAD_RETRY_DELAY_MILLIS,
    onRetry: suspend (attempt: Int, error: Throwable) -> Unit = { _, _ -> },
    block: suspend (attempt: Int) -> T,
): T {
    require(maxAttempts > 0) { "maxAttempts must be positive" }
    require(retryDelayMillis >= 0) { "retryDelayMillis must not be negative" }

    var attempt = 1
    while (true) {
        try {
            return block(attempt)
        } catch (error: Exception) {
            currentCoroutineContext().ensureActive()
            if (attempt >= maxAttempts || !error.isTransientDownloadFailure()) throw error

            onRetry(attempt, error)
            if (retryDelayMillis > 0) {
                delay(retryDelayMillis * attempt)
            }
            attempt++
        }
    }
}

internal fun Throwable.isTransientDownloadFailure(): Boolean {
    var error: Throwable? = this
    while (error != null) {
        if (error is ClosedByteChannelException) return true
        if (error is CancellationException) return false

        val errorName = error::class.simpleName.orEmpty()
        if (
            errorName == "SocketException" ||
            errorName == "SocketTimeoutException" ||
            errorName == "ConnectTimeoutException" ||
            errorName == "HttpRequestTimeoutException" ||
            errorName == "EOFException"
        ) {
            return true
        }

        val message = error.message.orEmpty().lowercase()
        if (
            message.contains("software caused connection abort") ||
            message.contains("connection reset") ||
            message.contains("connection closed") ||
            message.contains("broken pipe") ||
            message.contains("unexpected end of stream") ||
            message.contains("stream was reset")
        ) {
            return true
        }
        error = error.cause
    }
    return false
}

internal expect suspend fun prepareOutput(
    downloadRoot: PlatformFile,
    serverName: String,
    libraryName: String,
    seriesName: String,
    bookFileName: String,
): Pair<PlatformFile, Sink>

internal expect suspend fun deleteFile(file: PlatformFile)
