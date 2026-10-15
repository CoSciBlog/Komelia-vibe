package snd.komelia.ui.reader.image

import io.github.reactivecircus.cache4k.Cache
import io.github.reactivecircus.cache4k.CacheEvent.Evicted
import io.github.reactivecircus.cache4k.CacheEvent.Expired
import io.github.reactivecircus.cache4k.CacheEvent.Removed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Page loads belong to the cache, so cancelling a navigation waiter never restarts a load. */
internal class ReaderPageCache<K : Any, V : Any>(
    private val loadScope: CoroutineScope,
    cleanupScope: CoroutineScope,
    maximumSize: Long = 10,
    dispose: (V) -> Unit,
) {
    private val mutex = Mutex()
    private val generation = MutableStateFlow(0)
    private val cache = Cache.Builder<K, Deferred<V>>()
        .maximumCacheSize(maximumSize)
        .eventListener { event ->
            val job = when (event) {
                is Evicted -> event.value
                is Expired -> event.value
                is Removed -> event.value
                else -> null
            } ?: return@eventListener

            // Even a pending load may already own a native image. Let it finish and
            // close its result exactly once instead of losing it on cancellation.
            cleanupScope.launch { runCatching { job.await() }.getOrNull()?.let(dispose) }
        }
        .build()

    suspend fun getOrLoad(key: K, load: suspend () -> V): Deferred<V> = mutex.withLock {
        val loadGeneration = generation.value
        val result = cache.get(key)?.takeUnless { it.isCancelled }
            ?: loadScope.async { load() }.also { cache.put(key, it) }
        // stop() can clear the cache from another dispatcher while this call is
        // inserting a job. Do not leave that result behind after reader cleanup.
        if (generation.value != loadGeneration) cache.invalidate(key)
        result
    }

    fun clear() {
        generation.update { it + 1 }
        cache.invalidateAll()
    }
}

/** Nearest forward pages first; retain one preceding page for quick backtracking. */
internal fun readerPreloadIndices(currentIndex: Int, pageCount: Int): List<Int> {
    if (currentIndex !in 0 until pageCount) return emptyList()
    return (1..3).mapNotNull { distance ->
        (currentIndex + distance).takeIf { it < pageCount }
    } + listOfNotNull((currentIndex - 1).takeIf { it >= 0 })
}
