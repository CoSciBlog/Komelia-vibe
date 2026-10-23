package snd.komelia.ui.settings.offline.downloads

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_internal_storage
import org.jetbrains.compose.resources.stringResource
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import java.io.File
import kotlin.io.path.absolute

@Composable
internal actual fun rememberStorageLabel(file: PlatformFile): String {
    val context = LocalContext.current
    val internalLabel = stringResource(Res.string.ui_internal_storage)
    return remember(file, internalLabel) {
        when (val androidFile = file.androidFile) {
            is AndroidFile.FileWrapper -> labelForFile(context, androidFile.file, internalLabel)
            is AndroidFile.UriWrapper -> labelForUri(androidFile.uri)
        }
    }
}

private fun labelForFile(
    context: Context,
    file: File,
    internalLabel: String,
): String {
    val internalDir = context.filesDir.toPath().absolute()
    val currentPath = file.toPath().absolute()

    return if (currentPath.startsWith(internalDir)) internalLabel
    else currentPath.toString()

}

private const val AUTHORITY_DOCUMENT_EXTERNAL_STORAGE = "com.android.externalstorage.documents"
private fun labelForUri(
    uri: Uri,
): String {
    val authority = uri.authority
    return when {
        authority == AUTHORITY_DOCUMENT_EXTERNAL_STORAGE && DocumentsContract.isTreeUri(uri) ->
            uri.lastPathSegment ?: uri.toString()

        else -> uri.path ?: uri.toString()
    }
}
