package snd.komelia.ui.reader.image

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import snd.komelia.image.ReaderImage

/** Wait for initial rasterization as well as decoding and image processing. */
internal suspend fun ReaderImage.awaitPrepared() {
    combine(painter, error) { painter, error -> painter != null || error != null }.first { it }
}
