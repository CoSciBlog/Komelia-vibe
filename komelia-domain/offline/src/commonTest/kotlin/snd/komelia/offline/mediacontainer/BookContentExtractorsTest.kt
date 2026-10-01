package snd.komelia.offline.mediacontainer

import io.github.vinceglb.filekit.PlatformFile
import snd.komelia.offline.book.model.OfflineBook
import snd.komelia.offline.media.model.OfflineBookPage
import snd.komelia.offline.media.model.OfflineMedia
import snd.komga.client.book.KomgaBookId
import snd.komga.client.book.KomgaMediaStatus
import snd.komga.client.book.MediaProfile
import snd.komga.client.library.KomgaLibraryId
import snd.komga.client.series.KomgaSeriesId
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.Instant

class BookContentExtractorsTest {
    @Test
    fun downloadedPdfPageUsesLocalPdfExtractor() {
        val expected = byteArrayOf(1, 2, 3)
        val pdfExtractor = RecordingPdfExtractor(expected)
        val extractors = BookContentExtractors(
            divinaExtractors = emptyList(),
            epubExtractor = null,
            pdfExtractor = pdfExtractor,
        )

        val actual = extractors.getBookPage(
            book = offlineBook,
            media = pdfMedia,
            page = 2,
        )

        assertContentEquals(expected, actual)
        assertEquals(1, pdfExtractor.pageIndex)
        assertEquals(1600, pdfExtractor.width)
        assertEquals(2400, pdfExtractor.height)
        assertEquals(offlineBook.fileDownloadPath, pdfExtractor.file)
    }

    private class RecordingPdfExtractor(
        private val result: ByteArray,
    ) : PdfExtractor {
        var file: PlatformFile? = null
        var pageIndex: Int? = null
        var width: Int? = null
        var height: Int? = null

        override fun getPageBytes(
            file: PlatformFile,
            pageIndex: Int,
            width: Int?,
            height: Int?,
        ): ByteArray {
            this.file = file
            this.pageIndex = pageIndex
            this.width = width
            this.height = height
            return result
        }
    }

    private companion object {
        private val bookId = KomgaBookId("book-1")
        private val timestamp = Instant.fromEpochMilliseconds(0)
        private val offlineBook = OfflineBook(
            id = bookId,
            seriesId = KomgaSeriesId("series-1"),
            libraryId = KomgaLibraryId("library-1"),
            name = "book.pdf",
            number = 1,
            deleted = false,
            fileHash = "hash",
            oneshot = false,
            url = "book.pdf",
            size = "1 MiB",
            sizeBytes = 1,
            created = timestamp,
            lastModified = timestamp,
            remoteFileLastModified = timestamp,
            localFileLastModified = timestamp,
            remoteUnavailable = false,
            fileDownloadPath = PlatformFile("book.pdf"),
        )
        private val pdfMedia = OfflineMedia(
            bookId = bookId,
            status = KomgaMediaStatus.READY,
            mediaType = "application/pdf",
            mediaProfile = MediaProfile.PDF,
            comment = "",
            epubDivinaCompatible = false,
            pageCount = 2,
            pages = listOf(
                OfflineBookPage(bookId, "1", "image/png", 1200, 1800, null),
                OfflineBookPage(bookId, "2", "image/png", 1600, 2400, null),
            ),
        )
    }
}
