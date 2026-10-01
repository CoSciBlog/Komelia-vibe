package snd.komelia.ui.reader.image.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.InputChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_battery
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_bottom_center
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_bottom_left
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_bottom_right
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_clock
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_auto
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_black
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_blue
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_green
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_red
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_white
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_color_yellow
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_font_size
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_page_number
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_position
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_text_color
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_title
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_top_center
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_top_left
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_overlay_top_right
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import snd.komelia.settings.model.ReaderOverlayPosition
import snd.komelia.settings.model.ReaderOverlayTextColor
import snd.komelia.ui.common.components.SwitchWithLabel
import snd.komelia.ui.reader.image.ReaderState
import kotlin.math.roundToInt

@Composable
internal fun ReaderOverlaySettings(readerState: ReaderState) {
    val showClock by readerState.readerOverlayShowClock.collectAsState()
    val showBattery by readerState.readerOverlayShowBattery.collectAsState()
    val showPageNumber by readerState.readerOverlayShowPageNumber.collectAsState()
    val position by readerState.readerOverlayPosition.collectAsState()
    val textColor by readerState.readerOverlayTextColor.collectAsState()
    val fontSize by readerState.readerOverlayFontSize.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(Res.string.reader_overlay_title))
        SwitchWithLabel(
            checked = showClock,
            onCheckedChange = readerState::onReaderOverlayShowClockChange,
            label = { Text(stringResource(Res.string.reader_overlay_clock)) },
            contentPadding = PaddingValues(horizontal = 8.dp),
        )
        SwitchWithLabel(
            checked = showBattery,
            onCheckedChange = readerState::onReaderOverlayShowBatteryChange,
            label = { Text(stringResource(Res.string.reader_overlay_battery)) },
            contentPadding = PaddingValues(horizontal = 8.dp),
        )
        SwitchWithLabel(
            checked = showPageNumber,
            onCheckedChange = readerState::onReaderOverlayShowPageNumberChange,
            label = { Text(stringResource(Res.string.reader_overlay_page_number)) },
            contentPadding = PaddingValues(horizontal = 8.dp),
        )

        Text(stringResource(Res.string.reader_overlay_position))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            overlayPositions.forEach { (value, label) ->
                InputChip(
                    selected = position == value,
                    onClick = { readerState.onReaderOverlayPositionChange(value) },
                    label = { Text(stringResource(label)) },
                )
            }
        }

        Text(stringResource(Res.string.reader_overlay_text_color))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            overlayColors.forEach { (value, label) ->
                InputChip(
                    selected = textColor == value,
                    onClick = { readerState.onReaderOverlayTextColorChange(value) },
                    label = { Text(stringResource(label)) },
                )
            }
        }

        Text(stringResource(Res.string.reader_overlay_font_size, fontSize))
        Slider(
            value = fontSize.toFloat(),
            onValueChange = { readerState.onReaderOverlayFontSizeChange(it.roundToInt()) },
            valueRange = 10f..32f,
            steps = 21,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        )
    }
}

private val overlayPositions = listOf(
    ReaderOverlayPosition.TOP_LEFT to Res.string.reader_overlay_top_left,
    ReaderOverlayPosition.TOP_CENTER to Res.string.reader_overlay_top_center,
    ReaderOverlayPosition.TOP_RIGHT to Res.string.reader_overlay_top_right,
    ReaderOverlayPosition.BOTTOM_LEFT to Res.string.reader_overlay_bottom_left,
    ReaderOverlayPosition.BOTTOM_CENTER to Res.string.reader_overlay_bottom_center,
    ReaderOverlayPosition.BOTTOM_RIGHT to Res.string.reader_overlay_bottom_right,
)

private val overlayColors: List<Pair<ReaderOverlayTextColor, StringResource>> = listOf(
    ReaderOverlayTextColor.AUTO to Res.string.reader_overlay_color_auto,
    ReaderOverlayTextColor.WHITE to Res.string.reader_overlay_color_white,
    ReaderOverlayTextColor.BLACK to Res.string.reader_overlay_color_black,
    ReaderOverlayTextColor.RED to Res.string.reader_overlay_color_red,
    ReaderOverlayTextColor.YELLOW to Res.string.reader_overlay_color_yellow,
    ReaderOverlayTextColor.GREEN to Res.string.reader_overlay_color_green,
    ReaderOverlayTextColor.BLUE to Res.string.reader_overlay_color_blue,
)
