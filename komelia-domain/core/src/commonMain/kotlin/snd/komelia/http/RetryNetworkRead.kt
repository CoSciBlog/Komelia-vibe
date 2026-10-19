package snd.komelia.http

import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive

/** Retry a complete, repeatable read, including consumption of its response body. */
suspend fun <T> retryNetworkRead(block: suspend () -> T): T {
    var retries = 0
    while (true) {
        currentCoroutineContext().ensureActive()
        try {
            return block()
        } catch (error: Exception) {
            currentCoroutineContext().ensureActive()
            if (error is CancellationException || error !is IOException || retries == 2) throw error
            delay(250L shl retries)
            retries++
        }
    }
}
