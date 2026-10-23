package snd.komelia.ui.platform

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import snd.komelia.settings.model.AppLanguage
import java.util.Locale

@Composable
actual fun AppLanguageEnvironment(language: AppLanguage, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val systemConfiguration = LocalConfiguration.current
    val originalLocale = remember { Locale.getDefault() }
    val configuration = remember(systemConfiguration, language) {
        Configuration(systemConfiguration).apply {
            if (language != AppLanguage.SYSTEM) {
                setLocales(LocaleList(Locale.forLanguageTag(language.tag)))
            }
        }
    }
    // Compose Resources 1.12 also reads Locale.current / Locale.getDefault(),
    // including suspend getString calls outside the localized Context.
    Locale.setDefault(configuration.locales[0])
    DisposableEffect(Unit) { onDispose { Locale.setDefault(originalLocale) } }
    val localizedContext = remember(context, configuration) { context.createConfigurationContext(configuration) }
    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalConfiguration provides configuration,
        LocalContext provides localizedContext,
        content = content
    )
}
