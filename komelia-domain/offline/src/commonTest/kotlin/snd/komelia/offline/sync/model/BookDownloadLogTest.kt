package snd.komelia.offline.sync.model

import kotlinx.serialization.json.Json
import snd.komelia.offline.tasks.model.TaskData
import snd.komga.client.book.KomgaBookId
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertContains

class BookDownloadLogTest {
    private val bookId = KomgaBookId("book-42")

    @Test
    fun downloadedEntryIncludesBookOriginAndFreshResult() {
        val message = BookDownloadLog.downloaded(
            title = "Chapter 42",
            bookId = bookId,
            source = BookDownloadSource.READER_PRELOAD,
        )

        assertContains(message, "Book 'Chapter 42'")
        assertContains(message, "bookId=book-42")
        assertContains(message, "source=reader preload")
        assertContains(message, "result=freshly downloaded")
    }

    @Test
    fun localReaderEntryIsDistinguishableFromServerLoad() {
        val local = BookDownloadLog.loadedForReading("Chapter 42", bookId, local = true)
        val remote = BookDownloadLog.loadedForReading("Chapter 42", bookId, local = false)

        assertContains(local, "source=local/offline reader")
        assertContains(local, "result=already available locally")
        assertContains(remote, "source=online reader")
        assertContains(remote, "result=loaded from Komga server")
    }

    @Test
    fun skippedSeriesDownloadReportsExistingLocalBook() {
        val message = BookDownloadLog.alreadyLocal(
            title = "Chapter 42",
            bookId = bookId,
            source = BookDownloadSource.SERIES_DOWNLOAD,
        )

        assertContains(message, "source=offline series download")
        assertContains(message, "result=already available locally")
    }

    @Test
    fun persistedDownloadTaskWithoutSourceDefaultsToManualDownload() {
        val task = Json.decodeFromString<TaskData.DownloadBook>(
            """{"bookId":"book-42"}"""
        )

        assertEquals(bookId, task.bookId)
        assertEquals(BookDownloadSource.MANUAL_DOWNLOAD, task.source)
    }
}
