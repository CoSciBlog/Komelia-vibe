package snd.komelia.offline.sync

import snd.komga.client.book.KomgaBookId

/**
 * Start and manage long-running download jobs (e.g. using system specific APIs)
 * Optionally manages the display of system notifications
 */

interface PlatformDownloadManager {
    fun isNetworkAvailable(): Boolean = true
    suspend fun launchBookDownload(bookId: KomgaBookId)
    suspend fun cancelBookDownload(bookId: KomgaBookId)
}
