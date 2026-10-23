package snd.komelia.ui.series.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.book_download
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_download_confirm
import org.jetbrains.compose.resources.stringResource
import snd.komelia.komga.api.model.KomeliaSeries
import snd.komelia.ui.dialogs.ConfirmationDialog
import snd.komelia.ui.platform.cursorForHand

@Composable
fun SeriesDownloadButton(series: KomeliaSeries, onDownload: () -> Unit) {
    var showConfirmation by remember(series.id) { mutableStateOf(false) }
    ElevatedButton(
        onClick = { showConfirmation = true },
        modifier = Modifier.cursorForHand(),
    ) {
        Icon(Icons.Default.Download, contentDescription = null)
        Spacer(Modifier.width(3.dp))
        Text(stringResource(Res.string.book_download))
    }

    if (showConfirmation) {
        ConfirmationDialog(
            body = stringResource(Res.string.series_download_confirm, series.metadata.title),
            onDialogConfirm = onDownload,
            onDialogDismiss = { showConfirmation = false },
        )
    }
}
