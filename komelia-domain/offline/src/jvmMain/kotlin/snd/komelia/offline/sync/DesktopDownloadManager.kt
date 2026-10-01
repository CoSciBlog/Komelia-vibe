package snd.komelia.offline.sync

import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import snd.komelia.offline.sync.model.DownloadEvent
import snd.komelia.offline.sync.model.BookDownloadLog
import snd.komelia.offline.sync.model.BookDownloadSource
import snd.komelia.offline.sync.model.OfflineLogEntry.Companion.logError
import snd.komelia.offline.sync.model.OfflineLogEntry.Companion.logInfo
import snd.komelia.offline.sync.repository.LogJournalRepository
import snd.komga.client.book.KomgaBookId

private val downloadsSemaphore = Semaphore(4)

/**
 *  Do all the work on caller coroutine
 *  Tasks should be executed in the context of [snd.komelia.offline.tasks.TaskHandler]
 *  which would make them persistent across app restarts
 */
class DesktopDownloadManager(
    private val bookDownloadService: BookDownloadService,
    private val logsJournalRepository: LogJournalRepository,
    private val sharedEvents: MutableSharedFlow<DownloadEvent>,
) : PlatformDownloadManager {
    private val bookJobs = mutableMapOf<KomgaBookId, Job>()
    private val mutex = Mutex()

    override suspend fun launchBookDownload(bookId: KomgaBookId, source: BookDownloadSource) {
        try {
            mutex.withLock {
                val existing = bookJobs[bookId]
                if (existing != null) return
                bookJobs.put(bookId, currentCoroutineContext().job)
            }

            downloadsSemaphore.withPermit {
                bookDownloadService.downloadBook(bookId).collect {
                    sharedEvents.emit(it)
                    when (it) {
                        is DownloadEvent.BookDownloadProgress -> {}
                        is DownloadEvent.BookDownloadCompleted -> logsJournalRepository.logInfo {
                            BookDownloadLog.downloaded(it.book.metadata.title, it.book.id, source)
                        }
                        is DownloadEvent.BookDownloadError -> logsJournalRepository.logError(it.error) {
                            BookDownloadLog.downloadFailed(
                                it.book?.metadata?.title ?: it.bookId.value,
                                it.bookId,
                                source,
                            )
                        }
                    }
                }
            }

        } catch (e: Exception) {
            sharedEvents.emit(
                DownloadEvent.BookDownloadError(bookId = bookId, error = e)
            )
            currentCoroutineContext().ensureActive()
        } finally {
            mutex.withLock { bookJobs.remove(bookId) }
        }
    }

    override suspend fun cancelBookDownload(bookId: KomgaBookId) {
        mutex.withLock {
            val job = bookJobs.remove(bookId)
            job?.cancel()
        }
    }
}
