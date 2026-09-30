package snd.komelia.offline.sync

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DownloadRetentionPolicyTest {
    @Test
    fun seriesDownloadKeepsUnreadAndInProgressBooks() {
        assertTrue(shouldDownloadSeriesBook(downloadOnlyUnread = true, completed = false))
        assertFalse(shouldDownloadSeriesBook(downloadOnlyUnread = true, completed = true))
        assertTrue(shouldDownloadSeriesBook(downloadOnlyUnread = false, completed = true))
    }

    @Test
    fun cleanupRequiresBothSettingAndCompletedProgress() {
        assertTrue(shouldDeleteReadDownload(deleteReadBooks = true, completed = true))
        assertFalse(shouldDeleteReadDownload(deleteReadBooks = true, completed = false))
        assertFalse(shouldDeleteReadDownload(deleteReadBooks = false, completed = true))
    }

    @Test
    fun preloadStartsInsideConfiguredEndWindow() {
        assertTrue(
            shouldPreloadNextBook(
                preloadPages = 3,
                currentPage = 47,
                totalPages = 50,
                isOnline = true,
                isNextBookDownloaded = false,
                isPreloadAlreadyRequested = false,
            )
        )
        assertFalse(
            shouldPreloadNextBook(
                preloadPages = 3,
                currentPage = 46,
                totalPages = 50,
                isOnline = true,
                isNextBookDownloaded = false,
                isPreloadAlreadyRequested = false,
            )
        )
    }

    @Test
    fun preloadIsDisabledOrSkippedWhenUnsafeOrRedundant() {
        fun decision(
            preloadPages: Int = 3,
            isOnline: Boolean = true,
            downloaded: Boolean = false,
            requested: Boolean = false,
        ) = shouldPreloadNextBook(
            preloadPages = preloadPages,
            currentPage = 49,
            totalPages = 50,
            isOnline = isOnline,
            isNextBookDownloaded = downloaded,
            isPreloadAlreadyRequested = requested,
        )

        assertFalse(decision(preloadPages = 0))
        assertFalse(decision(preloadPages = 11))
        assertFalse(decision(isOnline = false))
        assertFalse(decision(downloaded = true))
        assertFalse(decision(requested = true))
    }
}
