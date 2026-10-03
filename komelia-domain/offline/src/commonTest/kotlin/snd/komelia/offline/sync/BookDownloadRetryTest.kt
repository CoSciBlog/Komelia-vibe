package snd.komelia.offline.sync

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookDownloadRetryTest {
    @Test
    fun retriesTransientConnectionAbortUntilDownloadSucceeds() = runTest {
        var attempts = 0
        val retriedAttempts = mutableListOf<Int>()

        val result = retryTransientDownload(
            maxAttempts = 3,
            retryDelayMillis = 0,
            onRetry = { attempt, _ -> retriedAttempts += attempt },
        ) {
            attempts++
            if (attempts < 3) error("Software caused connection abort")
            "downloaded"
        }

        assertEquals("downloaded", result)
        assertEquals(3, attempts)
        assertEquals(listOf(1, 2), retriedAttempts)
    }

    @Test
    fun stopsAfterConfiguredNumberOfAttempts() = runTest {
        var attempts = 0

        assertFailsWith<IllegalStateException> {
            retryTransientDownload(maxAttempts = 3, retryDelayMillis = 0) {
                attempts++
                error("Connection reset")
            }
        }

        assertEquals(3, attempts)
    }

    @Test
    fun doesNotRetryPermanentFailure() = runTest {
        var attempts = 0

        assertFailsWith<IllegalArgumentException> {
            retryTransientDownload(maxAttempts = 3, retryDelayMillis = 0) {
                attempts++
                throw IllegalArgumentException("Invalid download destination")
            }
        }

        assertEquals(1, attempts)
    }

    @Test
    fun detectsNestedTransientNetworkFailures() {
        val nested = IllegalStateException(
            "download failed",
            IllegalStateException("Unexpected end of stream"),
        )

        assertTrue(nested.isTransientDownloadFailure())
        assertFalse(IllegalStateException("HTTP 404").isTransientDownloadFailure())
    }
}
