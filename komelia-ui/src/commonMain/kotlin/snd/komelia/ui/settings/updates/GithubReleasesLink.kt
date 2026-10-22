package snd.komelia.ui.settings.updates

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.settings_updates_manual_download
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GithubReleasesLink() {
    val uriHandler = LocalUriHandler.current
    TextButton(onClick = { uriHandler.openUri("https://github.com/CoSciBlog/Komelia-vibe/releases") }) {
        Text(
            stringResource(Res.string.settings_updates_manual_download),
            textDecoration = TextDecoration.Underline,
        )
    }
}
