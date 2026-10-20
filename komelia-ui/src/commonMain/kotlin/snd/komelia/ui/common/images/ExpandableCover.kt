package snd.komelia.ui.common.images

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.cover_close
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.cover_open
import org.jetbrains.compose.resources.stringResource
import snd.komga.client.book.KomgaBookId
import snd.komga.client.series.KomgaSeriesId

@Composable
fun ExpandableBookCover(bookId: KomgaBookId, modifier: Modifier = Modifier) {
    ExpandableCover(bookId, modifier) { imageModifier ->
        BookThumbnail(bookId, modifier = imageModifier)
    }
}

@Composable
fun ExpandableSeriesCover(seriesId: KomgaSeriesId, modifier: Modifier = Modifier) {
    ExpandableCover(seriesId, modifier) { imageModifier ->
        SeriesThumbnail(seriesId, modifier = imageModifier)
    }
}

@Composable
private fun ExpandableCover(
    coverId: Any,
    modifier: Modifier,
    thumbnail: @Composable (Modifier) -> Unit,
) {
    var expanded by remember(coverId) { mutableStateOf(false) }
    val openLabel = stringResource(Res.string.cover_open)
    thumbnail(
        modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics { contentDescription = openLabel }
            .clickable(
                role = Role.Button,
                onClickLabel = openLabel,
                onClick = { expanded = true },
            )
    )

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Column(
                Modifier.fillMaxSize().background(Color.Black).padding(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { expanded = false }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                        Text(
                            stringResource(Res.string.cover_close),
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
                thumbnail(Modifier.fillMaxWidth().weight(1f))
            }
        }
    }
}
