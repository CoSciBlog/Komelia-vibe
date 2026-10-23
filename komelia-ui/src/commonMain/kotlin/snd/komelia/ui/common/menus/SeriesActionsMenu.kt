package snd.komelia.ui.common.menus

import org.jetbrains.compose.resources.getString
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_notification_launched_series_analysis
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_notification_launched_series_metadata_refresh

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_add_to_collection
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_analyze
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_delete_confirm_body
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_delete_confirm_title
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_download_confirm
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_refresh_metadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.book_mark_read
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.book_mark_unread
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.book_download
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_edit
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_delete_downloaded_all
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_delete_downloaded_read
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_delete_downloaded_all_confirm
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_delete_downloaded_read_confirm
import org.jetbrains.compose.resources.stringResource
import snd.komelia.AppNotification
import snd.komelia.AppNotifications
import snd.komelia.komga.api.KomgaSeriesApi
import snd.komelia.komga.api.model.KomeliaSeries
import snd.komelia.offline.tasks.OfflineTaskEmitter
import snd.komelia.ui.LocalViewModelFactory
import snd.komelia.ui.LocalKomfIntegration
import snd.komelia.ui.LocalKomgaState
import snd.komelia.ui.LocalOfflineAvailable
import snd.komelia.ui.LocalOfflineMode
import snd.komelia.ui.dialogs.ConfirmationDialog
import snd.komelia.ui.dialogs.collectionadd.AddToCollectionDialog
import snd.komelia.ui.dialogs.komf.identify.KomfIdentifyDialog
import snd.komelia.ui.dialogs.komf.reset.KomfResetSeriesMetadataDialog
import snd.komelia.ui.dialogs.series.edit.SeriesEditDialog

@Composable
fun SeriesActionsMenu(
    series: KomeliaSeries,
    actions: SeriesMenuActions,
    expanded: Boolean,
    showEditOption: Boolean,
    showDownloadOption: Boolean,
    onDismissRequest: () -> Unit,
) {
    val isAdmin = LocalKomgaState.current.authenticatedUser.collectAsState().value?.roleAdmin() ?: true
    val isOffline = LocalOfflineMode.current.collectAsState().value

    val factory = LocalViewModelFactory.current
    var deleteOnlyRead by remember(series.id) { mutableStateOf<Boolean?>(null) }
    deleteOnlyRead?.let { onlyRead ->
        ConfirmationDialog(
            title = stringResource(if (onlyRead) Res.string.series_delete_downloaded_read else Res.string.series_delete_downloaded_all),
            body = stringResource(
                if (onlyRead) Res.string.series_delete_downloaded_read_confirm else Res.string.series_delete_downloaded_all_confirm,
                series.metadata.title,
            ),
            onDialogConfirm = {
                deleteOnlyRead = null
                factory.deleteSeriesDownloads(series.id, onlyRead)
                onDismissRequest()
            },
            onDialogDismiss = {
                deleteOnlyRead = null
                onDismissRequest()
            },
            buttonConfirmColor = MaterialTheme.colorScheme.errorContainer,
        )
    }

    var showEditDialog by remember { mutableStateOf(false) }
    if (showEditDialog) {
        SeriesEditDialog(series, onDismissRequest = {
            showEditDialog = false
            onDismissRequest()
        })
    }

    var showAddToCollectionDialog by remember { mutableStateOf(false) }
    if (showAddToCollectionDialog) {
        AddToCollectionDialog(
            series = listOf(series),
            onDismissRequest = {
                showAddToCollectionDialog = false
                onDismissRequest()
            })
    }
    var showDownloadDialog by remember { mutableStateOf(false) }
    if (showDownloadDialog) {
        ConfirmationDialog(
            stringResource(Res.string.series_download_confirm, series.metadata.title),
            onDialogConfirm = { actions.download(series) },
            onDialogDismiss = { showDownloadDialog = false }
        )
    }

    val showDropdown = derivedStateOf {
        expanded &&
                !showEditDialog &&
                !showAddToCollectionDialog &&
                deleteOnlyRead == null
    }
    DropdownMenu(
        expanded = showDropdown.value,
        onDismissRequest = onDismissRequest
    ) {
        if (isAdmin && !isOffline) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.series_analyze)) },
                onClick = {
                    actions.analyze(series)
                    onDismissRequest()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.series_refresh_metadata)) },
                onClick = {
                    actions.refreshMetadata(series)
                    onDismissRequest()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.series_add_to_collection)) },
                onClick = { showAddToCollectionDialog = true },
            )
        }

        val isRead = series.booksReadCount == series.booksCount
        val isUnread = series.booksUnreadCount == series.booksCount
        if (!isRead) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.book_mark_read)) },
                onClick = {
                    actions.markAsRead(series)
                    onDismissRequest()
                },
            )
        }

        if (!isUnread) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.book_mark_unread)) },
                onClick = {
                    actions.markAsUnread(series)
                    onDismissRequest()
                },
            )
        }

        if (isAdmin && !isOffline && showEditOption) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.series_edit)) },
                onClick = { showEditDialog = true },
            )
        }

        val offlineAvailable = LocalOfflineAvailable.current
        if (!isOffline && showDownloadOption && offlineAvailable) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.book_download)) },
                onClick = { showDownloadDialog = true },
            )
        }

        if (offlineAvailable) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.series_delete_downloaded_all)) },
                onClick = { deleteOnlyRead = false },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.series_delete_downloaded_read)) },
                onClick = { deleteOnlyRead = true },
            )
        }
    }
}

data class SeriesMenuActions(
    val analyze: (KomeliaSeries) -> Unit,
    val refreshMetadata: (KomeliaSeries) -> Unit,
    val addToCollection: (KomeliaSeries) -> Unit,
    val markAsRead: (KomeliaSeries) -> Unit,
    val markAsUnread: (KomeliaSeries) -> Unit,
    val delete: (KomeliaSeries) -> Unit,
    val download: (KomeliaSeries) -> Unit,
    val deleteDownloaded: (KomeliaSeries) -> Unit,
) {
    constructor(
        seriesApi: KomgaSeriesApi,
        notifications: AppNotifications,
        taskEmitter: OfflineTaskEmitter?,
        scope: CoroutineScope,
    ) : this(
        analyze = {
            notifications.runCatchingToNotifications(scope) {
                seriesApi.analyze(it.id)
                notifications.add(AppNotification.Normal(getString(Res.string.ui_notification_launched_series_analysis)))
            }
        },
        refreshMetadata = {
            notifications.runCatchingToNotifications(scope) {
                seriesApi.refreshMetadata(it.id)
                notifications.add(AppNotification.Normal(getString(Res.string.ui_notification_launched_series_metadata_refresh)))
            }
        },
        addToCollection = { },
        markAsRead = {
            notifications.runCatchingToNotifications(scope) { seriesApi.markAsRead(it.id) }
        },
        markAsUnread = {
            notifications.runCatchingToNotifications(scope) { seriesApi.markAsUnread(it.id) }
        },
        delete = {
            notifications.runCatchingToNotifications(scope) { seriesApi.delete(it.id) }
        },
        download = { scope.launch { checkNotNull(taskEmitter).downloadSeries(it.id) } },
        deleteDownloaded = { scope.launch { checkNotNull(taskEmitter).deleteSeries(it.id) } }
    )
}
