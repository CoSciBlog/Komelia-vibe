package snd.komelia.ui.settings.server

import org.jetbrains.compose.resources.stringResource
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.library_empty_trash_confirm_title
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_are_you_sure_you_want_to_stop_komga
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_by_default_the_media_server_doesn_t_remove_information_for_media_right_away_this_helps_if_a_drive_is
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_cancel_all_currently_running_tasks
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_cancel_all_tasks
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_check_folders_for_new_or_removed_books_uses_the_last_modified_time_of_parent_folders
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_deep_scan
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_deep_scan_all_libraries
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_delete_items_marked_as_unavailable
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_empty
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_empty_trash_for_all_libraries
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_force_the_scanner_to_compare_all_scanned_books_with_the_ones_stored_in_the_database
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_scan
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_scan_all_libraries
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_server_management
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_shut_down_server
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_shutdown
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_stop
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_stop_komga_application_process
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.user_add_cancel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import snd.komelia.ui.dialogs.ConfirmationDialog

@Composable
fun ServerManagementContent(
    onScanAllLibraries: (deep: Boolean) -> Unit,
    onEmptyTrash: () -> Unit,
    onCancelAllTasks: () -> Unit,
    onShutdown: () -> Unit
) {

    var showEmptyTrashDialog by remember { mutableStateOf(false) }
    var showShutdownDialog by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(stringResource(Res.string.ui_server_management), style = MaterialTheme.typography.titleLarge)
        HorizontalDivider()
        Button(
            title = stringResource(Res.string.ui_scan_all_libraries),
            description = stringResource(Res.string.ui_check_folders_for_new_or_removed_books_uses_the_last_modified_time_of_parent_folders),
            buttonText = stringResource(Res.string.ui_scan),
            level = WarningLevel.NORMAL,
            onClick = { onScanAllLibraries(false) }
        )
        HorizontalDivider()
        Button(
            title = stringResource(Res.string.ui_deep_scan_all_libraries),
            description = stringResource(Res.string.ui_force_the_scanner_to_compare_all_scanned_books_with_the_ones_stored_in_the_database),
            buttonText = stringResource(Res.string.ui_deep_scan),
            level = WarningLevel.NORMAL,
            onClick = { onScanAllLibraries(true) }
        )
        HorizontalDivider()
        Button(
            title = stringResource(Res.string.ui_empty_trash_for_all_libraries),
            description = stringResource(Res.string.ui_delete_items_marked_as_unavailable),
            buttonText = stringResource(Res.string.ui_empty),
            level = WarningLevel.NORMAL,
            onClick = { showEmptyTrashDialog = true }
        )
        HorizontalDivider()
        Button(
            title = stringResource(Res.string.ui_cancel_all_tasks),
            description = stringResource(Res.string.ui_cancel_all_currently_running_tasks),
            buttonText = stringResource(Res.string.user_add_cancel),
            level = WarningLevel.WARNING,
            onClick = { onCancelAllTasks() }
        )
        HorizontalDivider()
        Button(
            title = stringResource(Res.string.ui_shutdown),
            description = stringResource(Res.string.ui_stop_komga_application_process),
            buttonText = stringResource(Res.string.ui_shutdown),
            level = WarningLevel.DANGER,
            onClick = { showShutdownDialog = true }
        )
        HorizontalDivider()

        if (showEmptyTrashDialog) {
            ConfirmationDialog(
                title = stringResource(Res.string.library_empty_trash_confirm_title),
                body = stringResource(Res.string.ui_by_default_the_media_server_doesn_t_remove_information_for_media_right_away_this_helps_if_a_drive_is),
                buttonConfirm = stringResource(Res.string.ui_empty),
                buttonCancel = stringResource(Res.string.user_add_cancel),
                onDialogConfirm = onEmptyTrash,
                onDialogDismiss = { showEmptyTrashDialog = false }
            )
        }

        if (showShutdownDialog) {
            ConfirmationDialog(
                title = stringResource(Res.string.ui_shut_down_server),
                body = stringResource(Res.string.ui_are_you_sure_you_want_to_stop_komga),
                buttonConfirm = stringResource(Res.string.ui_stop),
                buttonCancel = stringResource(Res.string.user_add_cancel),
                buttonConfirmColor = MaterialTheme.colorScheme.errorContainer,
                onDialogConfirm = onShutdown,
                onDialogDismiss = { showShutdownDialog = false }
            )
        }

    }
}

@Composable
private fun Button(
    title: String,
    description: String,
    buttonText: String,
    level: WarningLevel,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.labelLarge)
        }

        val colors = when (level) {
            WarningLevel.NORMAL -> ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            WarningLevel.WARNING -> ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )

            WarningLevel.DANGER -> ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        FilledTonalButton(
            onClick = onClick,
            colors = colors,
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
        ) {
            Text(buttonText)
        }

    }
}


private enum class WarningLevel {
    NORMAL,
    WARNING,
    DANGER
}