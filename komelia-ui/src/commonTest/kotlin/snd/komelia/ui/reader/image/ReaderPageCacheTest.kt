package snd.komelia.ui.reader.image

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderPageCacheTest {
    @Test
    fun simultaneousRequestsShareOneLoad() = runTest {
        val ready = CompletableDeferred<String>()
        var loads = 0
        val cache = ReaderPageCache<Int, String>(backgroundScope, backgroundScope) {}
        val requests = List(20) {
            async { cache.getOrLoad(2) { loads++; ready.await() } }
        }
        runCurrent()
        val first = requests.first().await()
        requests.forEach { assertSame(first, it.await()) }
        assertEquals(1, loads)
        ready.complete("prepared page")
        assertEquals("prepared page", first.await())
    }

    @Test
    fun turningPageDoesNotCancelOrRestartItsPreload() = runTest {
        val ready = CompletableDeferred<String>()
        var loads = 0
        val cache = ReaderPageCache<Int, String>(backgroundScope, backgroundScope) {}
        val preload = cache.getOrLoad(2) { loads++; ready.await() }
        val oldNavigation = async { preload.await() }
        runCurrent()
        oldNavigation.cancelAndJoin()
        assertFalse(preload.isCancelled)

        val newNavigation = cache.getOrLoad(2) { error("must reuse preload") }
        assertSame(preload, newNavigation)
        ready.complete("decoded and processed")
        assertEquals("decoded and processed", newNavigation.await())
        assertEquals(1, loads)
    }

    @Test
    fun clearingPendingLoadClosesItsEventualResultOnce() = runTest {
        val ready = CompletableDeferred<String>()
        val closed = mutableListOf<String>()
        val cache = ReaderPageCache<Int, String>(backgroundScope, backgroundScope, dispose = closed::add)
        cache.getOrLoad(1) { ready.await() }
        runCurrent()
        cache.clear()
        cache.clear()
        runCurrent()
        assertEquals(emptyList(), closed)

        ready.complete("native image")
        runCurrent()
        assertEquals(listOf("native image"), closed)
    }

    @Test
    fun cacheEvictionDisposesImagesAndRetainsRecentPages() = runTest {
        val closed = mutableListOf<String>()
        val cache = ReaderPageCache<Int, String>(
            backgroundScope, backgroundScope, maximumSize = 2, dispose = closed::add
        )
        cache.getOrLoad(1) { "one" }.await()
        val second = cache.getOrLoad(2) { "two" }
        second.await()
        cache.getOrLoad(3) { "three" }.await()
        runCurrent()
        assertEquals(listOf("one"), closed)
        assertSame(second, cache.getOrLoad(2) { error("still cached") })
        cache.clear()
        runCurrent()
        assertEquals(setOf("one", "two", "three"), closed.toSet())
        assertEquals(3, closed.size)
    }

    @Test
    fun preloadsThreeForwardPagesBeforeOnePreviousPage() {
        assertEquals(listOf(5, 6, 7, 3), readerPreloadIndices(4, 20))
        assertEquals(listOf(1, 2, 3), readerPreloadIndices(0, 20))
        assertEquals(listOf(19, 17), readerPreloadIndices(18, 20))
        assertEquals(listOf(18), readerPreloadIndices(19, 20))
        assertEquals(emptyList(), readerPreloadIndices(0, 1))
        assertEquals(emptyList(), readerPreloadIndices(0, 0))
        assertEquals(emptyList(), readerPreloadIndices(-1, 20))
        assertEquals(emptyList(), readerPreloadIndices(20, 20))
    }
}
