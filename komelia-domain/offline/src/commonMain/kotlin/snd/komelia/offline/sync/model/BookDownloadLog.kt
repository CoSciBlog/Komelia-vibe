package snd.komelia.offline.sync.model

import kotlinx.serialization.Serializable
import snd.komga.client.book.KomgaBookId

@Serializable
enum class BookDownloadSource(val displayName: String) {
    MANUAL_DOWNLOAD("download feature"),
    SERIES_DOWNLOAD("offline series download"),
    READER_PRELOAD("reader preload"),
}

object BookDownloadLog {
    fun downloaded(
        title: String,
        bookId: KomgaBookId,
        source: BookDownloadSource,
    ) = entry(
        title = title,
        bookId = bookId,
        source = source.displayName,
        result = "freshly downloaded",
    )

    fun alreadyLocal(
        title: String,
        bookId: KomgaBookId,
        source: BookDownloadSource,
    ) = entry(
        title = title,
        bookId = bookId,
        source = source.displayName,
        result = "already available locally",
    )

    fun downloadFailed(
        title: String,
        bookId: KomgaBookId,
        source: BookDownloadSource,
    ) = entry(
        title = title,
        bookId = bookId,
        source = source.displayName,
        result = "download failed",
    )

    fun loadedForReading(
        title: String,
        bookId: KomgaBookId,
        local: Boolean,
    ) = entry(
        title = title,
        bookId = bookId,
        source = if (local) "local/offline reader" else "online reader",
        result = if (local) "already available locally" else "loaded from Komga server",
    )

    private fun entry(
        title: String,
        bookId: KomgaBookId,
        source: String,
        result: String,
    ) = buildString {
        append("Book '$title'")
        append("\nbookId=${bookId.value}")
        append("\nsource=$source")
        append("\nresult=$result")
    }
}
