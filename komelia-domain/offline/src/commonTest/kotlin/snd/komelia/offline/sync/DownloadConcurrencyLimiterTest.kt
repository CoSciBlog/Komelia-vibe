package snd.komelia.offline.sync

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import snd.komelia.offline.settings.DEFAULT_CONCURRENT_DOWNLOADS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadConcurrencyLimiterTest {
    @Test
    fun everySupportedLimitAndOutOfRangeValuesBoundActiveDownloads() = runTest {
        for (configured in listOf(-1, 0) + (1..10) + listOf(11, Int.MAX_VALUE)) {
            val limiter = DownloadConcurrencyLimiter(flowOf(configured))
            var active = 0
            val jobs = List(12) {
                launch {
                    limiter.withPermit {
                        active++
                        try { awaitCancellation() } finally { active-- }
                    }
                }
            }
            runCurrent()
            assertEquals(configured.coerceIn(1, 10), active, "limit=$configured")
            jobs.forEach { it.cancel() }
            jobs.forEach { it.join() }
            assertEquals(0, active)
        }
    }

    @Test
    fun defaultAllowsTwoDownloadsAndCompletionStartsTheNextBook() = runTest {
        assertEquals(2, DEFAULT_CONCURRENT_DOWNLOADS)
        val limiter = DownloadConcurrencyLimiter(flowOf(DEFAULT_CONCURRENT_DOWNLOADS))
        val finish = List(3) { CompletableDeferred<Unit>() }
        val started = mutableListOf<Int>()
        val jobs = List(3) { index ->
            launch { limiter.withPermit { started += index; finish[index].await() } }
        }
        runCurrent()
        assertEquals(listOf(0, 1), started)
        finish[0].complete(Unit)
        runCurrent()
        assertEquals(listOf(0, 1, 2), started)
        finish.forEach { it.complete(Unit) }
        jobs.forEach { it.join() }
    }

    @Test
    fun increasingLimitStartsWaitingBooksWithoutFinishingActiveOnes() = runTest {
        val maximum = MutableStateFlow(1)
        val limiter = DownloadConcurrencyLimiter(maximum)
        var started = 0
        val jobs = List(10) { launch { limiter.withPermit { started++; awaitCancellation() } } }
        runCurrent()
        assertEquals(1, started)
        maximum.value = 10
        runCurrent()
        assertEquals(10, started)
        jobs.forEach { assertTrue(it.isActive); it.cancel() }
        jobs.forEach { it.join() }
    }

    @Test
    fun decreasingLimitLetsActiveBooksFinishAndHoldsNewBooks() = runTest {
        val maximum = MutableStateFlow(3)
        val limiter = DownloadConcurrencyLimiter(maximum)
        val finish = List(4) { CompletableDeferred<Unit>() }
        val started = mutableListOf<Int>()
        val jobs = List(4) { index ->
            launch { limiter.withPermit { started += index; finish[index].await() } }
        }
        runCurrent()
        maximum.value = 1
        runCurrent()
        assertEquals(listOf(0, 1, 2), started)
        jobs.take(3).forEach { assertTrue(it.isActive) }
        for (index in 0..1) {
            finish[index].complete(Unit)
            runCurrent()
            assertEquals(listOf(0, 1, 2), started)
        }
        finish[2].complete(Unit)
        runCurrent()
        assertEquals(listOf(0, 1, 2, 3), started)
        finish[3].complete(Unit)
        jobs.forEach { it.join() }
    }

    @Test
    fun cancellingQueuedBookDoesNotConsumeASlotOrBlockOtherBooks() = runTest {
        val limiter = DownloadConcurrencyLimiter(flowOf(1))
        val active = launch { limiter.withPermit { awaitCancellation() } }
        var cancelledStarted = false
        val cancelled = launch { limiter.withPermit { cancelledStarted = true } }
        var nextStarted = false
        val next = launch { limiter.withPermit { nextStarted = true } }
        runCurrent()
        cancelled.cancelAndJoin()
        assertFalse(cancelledStarted)
        assertFalse(nextStarted)
        active.cancelAndJoin()
        next.join()
        assertTrue(nextStarted)
    }

    @Test
    fun failureReleasesSlotForTheNextBook() = runTest {
        val limiter = DownloadConcurrencyLimiter(flowOf(1))
        assertFailsWith<IllegalStateException> {
            limiter.withPermit { error("transfer failed") }
        }
        var started = false
        limiter.withPermit { started = true }
        assertTrue(started)
    }
}
