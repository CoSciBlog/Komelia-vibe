package snd.komelia.offline.series

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import snd.komelia.offline.readprogress.OfflineReadProgress
import snd.komga.client.book.KomgaBookId
import snd.komga.client.series.KomgaSeriesId
import snd.komga.client.user.KomgaUserId
import kotlin.coroutines.cancellation.CancellationException

/** Removes local downloads, with a final transactional check before discarding progress. */
class SeriesDownloadDeletion(
    private val localBookIds: suspend (KomgaSeriesId) -> List<KomgaBookId>,
    private val progressForBook: suspend (KomgaBookId) -> List<OfflineReadProgress>,
    private val canContactServer: (KomgaUserId) -> Boolean,
    private val synchronizeProgress: suspend (OfflineReadProgress) -> OfflineReadProgress?,
    private val remotelyCompleted: suspend (KomgaBookId) -> Boolean,
    private val deleteIfUnchanged: suspend (KomgaBookId, List<OfflineReadProgress>, KomgaUserId) -> Boolean,
    private val onError: suspend (KomgaBookId, Exception) -> Unit,
) {
    private val mutex = Mutex()

    data class Result(val deleted: Int, val retained: Int)

    suspend fun execute(seriesId: KomgaSeriesId, userId: KomgaUserId, onlyRead: Boolean): Result = mutex.withLock {
        var deleted = 0
        val ids = localBookIds(seriesId)
        for (bookId in ids) {
            try {
                val progress = progressForBook(bookId)
                // Another account's progress cannot be confirmed using the active account.
                if (progress.any { it.userId != userId }) continue
                val local = progress.singleOrNull()
                val confirmed = if (local != null) {
                    if (!canContactServer(userId)) continue
                    synchronizeProgress(local) ?: continue
                } else null
                val completed = confirmed?.completed ?: if (onlyRead && canContactServer(userId)) {
                    remotelyCompleted(bookId)
                } else false
                if (onlyRead && !completed) continue

                if (deleteIfUnchanged(bookId, listOfNotNull(confirmed), userId)) deleted++
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                currentCoroutineContext().ensureActive()
                onError(bookId, error)
            }
        }
        Result(deleted, ids.size - deleted)
    }
}
