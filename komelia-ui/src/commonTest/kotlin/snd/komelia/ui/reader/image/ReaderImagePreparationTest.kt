package snd.komelia.ui.reader.image

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import snd.komelia.image.KomeliaImage
import snd.komelia.image.ReaderImage
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderImagePreparationTest {
    @Test
    fun preparationWaitsForRenderingAfterDecodingAndProcessing() = runTest {
        val image = TestImage()
        val preparation = async { image.awaitPrepared() }
        image.originalSize.value = IntSize(1000, 1500)
        image.displaySize.value = IntSize(500, 750)
        runCurrent()
        assertFalse(preparation.isCompleted)

        image.painter.value = object : Painter() {
            override val intrinsicSize = Size(500f, 750f)
            override fun DrawScope.onDraw() = Unit
        }
        preparation.await()
        assertTrue(preparation.isCompleted)
    }

    @Test
    fun processingFailureDoesNotStallFollowingPreloads() = runTest {
        val image = TestImage()
        val preparation = async { image.awaitPrepared() }
        runCurrent()
        image.error.value = IllegalStateException("cannot decode page")
        preparation.await()
        assertTrue(preparation.isCompleted)
    }

    private class TestImage : ReaderImage {
        override val pageId = ReaderImage.PageId("book", 1)
        override val originalSize = MutableStateFlow<IntSize?>(null)
        override val displaySize = MutableStateFlow<IntSize?>(null)
        override val currentSize = MutableStateFlow<IntSize?>(null)
        override val painter = MutableStateFlow<Painter?>(null)
        override val error = MutableStateFlow<Throwable?>(null)
        override fun requestUpdate(maxDisplaySize: IntSize, zoomFactor: Float, visibleDisplaySize: IntRect) = Unit
        override suspend fun getOriginalImageSize(): Result<IntSize> = error("not used")
        override suspend fun getOriginalImage(): Result<KomeliaImage> = error("not used")
        override fun close() = Unit
    }
}
