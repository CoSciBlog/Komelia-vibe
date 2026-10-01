package snd.komelia.ui.reader.image.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import snd.komelia.settings.model.ReaderOverlayPosition
import snd.komelia.settings.model.ReaderOverlayTextColor
import kotlin.time.Clock

@Composable
internal fun ReaderStatusOverlay(
    showClock: Boolean,
    showBattery: Boolean,
    showPageNumber: Boolean,
    position: ReaderOverlayPosition,
    textColor: ReaderOverlayTextColor,
    fontSize: Int,
    currentPage: Int,
    totalPages: Int,
) {
    val currentTime by produceState(initialValue = formattedCurrentTime(), showClock) {
        if (!showClock) return@produceState
        while (isActive) {
            value = formattedCurrentTime()
            delay(30_000)
        }
    }
    val batteryPercentage = rememberBatteryPercentage()
    val parts = buildList {
        if (showBattery && batteryPercentage != null) add("$batteryPercentage%")
        if (showClock) add(currentTime)
        if (showPageNumber && totalPages > 0) add("${currentPage.coerceIn(1, totalPages)}/$totalPages")
    }
    if (parts.isEmpty()) return

    val foreground = when (textColor) {
        ReaderOverlayTextColor.AUTO -> MaterialTheme.colorScheme.onSurface
        ReaderOverlayTextColor.WHITE -> Color.White
        ReaderOverlayTextColor.BLACK -> Color.Black
        ReaderOverlayTextColor.RED -> Color(0xffef5350)
        ReaderOverlayTextColor.YELLOW -> Color(0xffffd54f)
        ReaderOverlayTextColor.GREEN -> Color(0xff66bb6a)
        ReaderOverlayTextColor.BLUE -> Color(0xff42a5f5)
    }
    val background = when (textColor) {
        ReaderOverlayTextColor.BLACK -> Color.White.copy(alpha = 0.78f)
        ReaderOverlayTextColor.AUTO -> MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
        else -> Color.Black.copy(alpha = 0.68f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(12.dp),
        contentAlignment = position.toAlignment(),
    ) {
        Text(
            text = parts.joinToString(" · "),
            color = foreground,
            fontSize = fontSize.coerceIn(10, 32).sp,
            modifier = Modifier
                .background(background, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private fun formattedCurrentTime(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
}

private fun ReaderOverlayPosition.toAlignment(): Alignment = when (this) {
    ReaderOverlayPosition.TOP_LEFT -> Alignment.TopStart
    ReaderOverlayPosition.TOP_CENTER -> Alignment.TopCenter
    ReaderOverlayPosition.TOP_RIGHT -> Alignment.TopEnd
    ReaderOverlayPosition.BOTTOM_LEFT -> Alignment.BottomStart
    ReaderOverlayPosition.BOTTOM_CENTER -> Alignment.BottomCenter
    ReaderOverlayPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
}

@Composable
internal expect fun rememberBatteryPercentage(): Int?
