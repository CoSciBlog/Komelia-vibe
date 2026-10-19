package snd.komelia.http

import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.ClosedByteChannelException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RetryNetworkReadTest {
    @Test
    fun retriesTheWholeReadAfterAnAbortedBody() = runTest {
        var attempts = 0
        val bytes = retryNetworkRead {
            attempts++
            if (attempts < 3) throw ClosedByteChannelException(IOException("Software caused connection abort"))
            byteArrayOf(1, 2, 3)
        }
        assertEquals(listOf<Byte>(1, 2, 3), bytes.toList())
        assertEquals(3, attempts)
        assertEquals(750L, testScheduler.currentTime)
    }

    @Test
    fun permanentFailureIsBoundedAndPreservesTheError() = runTest {
        var attempts = 0
        val error = IOException("connection abort")
        val thrown = assertFailsWith<IOException> {
            retryNetworkRead { attempts++; throw error }
        }
        assertEquals(3, attempts)
        assertSame(error, thrown)
    }

    @Test
    fun cancellationAndApplicationErrorsAreNeverRetried() = runTest {
        for (error in listOf(CancellationException("left screen"), IllegalArgumentException("bad data"))) {
            var attempts = 0
            val thrown = assertFailsWith<Exception> {
                retryNetworkRead { attempts++; throw error }
            }
            assertSame(error, thrown)
            assertEquals(1, attempts)
        }
    }

    @Test
    fun cancellationDuringBackoffStopsFurtherRequests() = runTest {
        var attempts = 0
        val job = launch { retryNetworkRead { attempts++; throw IOException("abort") } }
        runCurrent()
        job.cancelAndJoin()
        assertEquals(1, attempts)
    }
}
