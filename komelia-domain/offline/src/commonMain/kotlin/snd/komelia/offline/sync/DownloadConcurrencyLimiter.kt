package snd.komelia.offline.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import snd.komelia.offline.settings.MAX_CONCURRENT_DOWNLOADS
import snd.komelia.offline.settings.MIN_CONCURRENT_DOWNLOADS

/** Shares a live limit across book downloads without interrupting active transfers. */
internal class DownloadConcurrencyLimiter(private val limit: Flow<Int>) {
    private val activeDownloads = MutableStateFlow(0)
    private val acquisitionMutex = Mutex()

    suspend fun <T> withPermit(block: suspend () -> T): T {
        acquisitionMutex.withLock {
            combine(activeDownloads, limit) { active, maximum ->
                active < maximum.coerceIn(MIN_CONCURRENT_DOWNLOADS, MAX_CONCURRENT_DOWNLOADS)
            }.first { it }
            activeDownloads.update { it + 1 }
        }
        try {
            return block()
        } finally {
            // Releasing must work even when the download's coroutine was cancelled.
            activeDownloads.update { it - 1 }
        }
    }
}
