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
}
