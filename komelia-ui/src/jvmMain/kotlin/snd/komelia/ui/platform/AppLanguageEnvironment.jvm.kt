package snd.komelia.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import snd.komelia.settings.model.AppLanguage
import java.util.Locale

@Composable
actual fun AppLanguageEnvironment(language: AppLanguage, content: @Composable () -> Unit) {
    val systemLocale = remember { Locale.getDefault() }
    val selected = if (language == AppLanguage.SYSTEM) AppLanguage.forDeviceTag(systemLocale.toLanguageTag()) else language
    Locale.setDefault(Locale.forLanguageTag(selected.tag))
    DisposableEffect(Unit) { onDispose { Locale.setDefault(systemLocale) } }
    CompositionLocalProvider(LocalAppLanguage provides language, content = content)
}
