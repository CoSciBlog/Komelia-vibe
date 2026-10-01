package snd.komelia.offline.sync

import snd.komga.client.book.KomgaBookId
import snd.komelia.offline.sync.model.BookDownloadSource

/**
 * Start and manage long-running download jobs (e.g. using system specific APIs)
 * Optionally manages the display of system notifications
 */

interface PlatformDownloadManager {
    fun isNetworkAvailable(): Boolean = true
    suspend fun launchBookDownload(bookId: KomgaBookId, source: BookDownloadSource)
    suspend fun cancelBookDownload(bookId: KomgaBookId)
}
