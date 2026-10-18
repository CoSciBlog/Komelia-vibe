package snd.komelia.offline.series

import kotlinx.coroutines.test.runTest
import snd.komelia.offline.readprogress.OfflineReadProgress
import snd.komga.client.book.KomgaBookId
import snd.komga.client.series.KomgaSeriesId
import snd.komga.client.user.KomgaUserId
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class SeriesDownloadDeletionTest {
    private val user = KomgaUserId("reader")
    private val series = KomgaSeriesId("series")
    private val a = KomgaBookId("a")
    private val b = KomgaBookId("b")
    private val c = KomgaBookId("c")

    @Test
    fun allDownloadsUsesEveryLocalBookAndDoesNotFetchRemoteBooksWithoutProgress() = runTest {
        val f = Fixture()
        assertEquals(SeriesDownloadDeletion.Result(3, 0), f.service.execute(series, user, false))
        assertEquals(setOf(a, b, c), f.deleted)
        assertEquals(0, f.remoteLookups)
    }

    @Test
    fun readOnlyRemovesCompletedBooksAndRetainsUnreadAndInProgress() = runTest {
        val f = Fixture()
        f.progress[b] = listOf(progress(b, completed = false))
        f.remoteRead += a
        assertEquals(SeriesDownloadDeletion.Result(1, 2), f.service.execute(series, user, true))
        assertEquals(setOf(a), f.deleted)
        assertEquals(false, f.progress[b]?.single()?.completed)
    }

    @Test
    fun offlineDeletionRetainsAllUnconfirmedProgressAndCanRemoveUnstartedDownloads() = runTest {
        val f = Fixture(online = false)
        f.progress[a] = listOf(progress(a, completed = true))
        f.progress[b] = listOf(progress(b, completed = false))
        assertEquals(SeriesDownloadDeletion.Result(1, 2), f.service.execute(series, user, false))
        assertEquals(setOf(c), f.deleted)
        assertEquals(0, f.syncCalls)
        assertEquals(0, f.remoteLookups)
    }

    @Test
    fun anotherUsersProgressIsNeverDiscardedOrSynchronizedAsCurrentUser() = runTest {
        val f = Fixture()
        f.progress[a] = listOf(progress(a, true), progress(a, true).copy(userId = KomgaUserId("other")))
        f.service.execute(series, user, false)
        assertEquals(setOf(b, c), f.deleted)
        assertEquals(0, f.syncCalls)
    }

    @Test
    fun synchronizationFailureAndConcurrentProgressChangesRetainDownloads() = runTest {
        val f = Fixture()
        f.progress[a] = listOf(progress(a, true))
        f.failSync = true
        f.concurrentChanges += b
        assertEquals(SeriesDownloadDeletion.Result(1, 2), f.service.execute(series, user, false))
        assertEquals(setOf(c), f.deleted)
    }

    @Test
    fun newerRemoteUnreadProgressPreventsReadOnlyDeletion() = runTest {
        val f = Fixture()
        f.progress[a] = listOf(progress(a, true))
        f.syncedProgress = progress(a, false).copy(lastModifiedDate = Instant.fromEpochSeconds(20))
        assertEquals(SeriesDownloadDeletion.Result(0, 3), f.service.execute(series, user, true))
    }

    @Test
    fun deletionChecksTheConfirmedSnapshotAfterRemoteReconciliation() = runTest {
        val f = Fixture()
        f.progress[a] = listOf(progress(a, false))
        f.syncedProgress = progress(a, true).copy(lastModifiedDate = Instant.fromEpochSeconds(20))
        assertEquals(SeriesDownloadDeletion.Result(1, 2), f.service.execute(series, user, true))
        assertEquals(setOf(a), f.deleted)
    }

    @Test
    fun remoteFailureRetainsDownloadAndReportsErrorWithoutStoppingOtherBooks() = runTest {
        val f = Fixture()
        f.remoteRead += c
        f.remoteFailure = a
        assertEquals(SeriesDownloadDeletion.Result(1, 2), f.service.execute(series, user, true))
        assertEquals(listOf(a), f.errors)
        assertEquals(setOf(c), f.deleted)
    }

    @Test
    fun cancellationDoesNotDeleteOrContinueWithOtherBooks() = runTest {
        val f = Fixture()
        f.progress[a] = listOf(progress(a, true))
        f.cancelSync = true
        assertFailsWith<CancellationException> { f.service.execute(series, user, false) }
        assertEquals(emptySet(), f.deleted)
    }

    private fun progress(id: KomgaBookId, completed: Boolean) = OfflineReadProgress(
        bookId = id, userId = user, page = 3, completed = completed,
        createdDate = Instant.fromEpochSeconds(1), lastModifiedDate = Instant.fromEpochSeconds(10),
    )

    private inner class Fixture(val online: Boolean = true) {
        val progress = mutableMapOf<KomgaBookId, List<OfflineReadProgress>>()
        val remoteRead = mutableSetOf<KomgaBookId>()
        val deleted = mutableSetOf<KomgaBookId>()
        val concurrentChanges = mutableSetOf<KomgaBookId>()
        val errors = mutableListOf<KomgaBookId>()
        var syncCalls = 0
        var remoteLookups = 0
        var failSync = false
        var cancelSync = false
        var remoteFailure: KomgaBookId? = null
        var syncedProgress: OfflineReadProgress? = null
        val service = SeriesDownloadDeletion(
            localBookIds = { requested -> assertEquals(series, requested); listOf(a, b, c) },
            progressForBook = { progress[it].orEmpty() },
            canContactServer = { online && it == user },
            synchronizeProgress = { local ->
                syncCalls++
                if (cancelSync) throw CancellationException("cancelled")
                if (failSync) null else (syncedProgress ?: local).also { progress[local.bookId] = listOf(it) }
            },
            remotelyCompleted = { id ->
                remoteLookups++
                if (id == remoteFailure) error("Server unavailable")
                id in remoteRead
            },
            deleteIfUnchanged = { id, expected, actor ->
                assertEquals(user, actor)
                if (id in concurrentChanges || progress[id].orEmpty() != expected) false
                else { deleted += id; true }
            },
            onError = { id, _ -> errors += id },
        )
    }
}
