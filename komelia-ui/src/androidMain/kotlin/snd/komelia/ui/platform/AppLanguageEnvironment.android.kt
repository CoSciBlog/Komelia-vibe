package snd.komelia.ui.platform

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import snd.komelia.settings.model.AppLanguage
import java.util.Locale

@Composable
actual fun AppLanguageEnvironment(language: AppLanguage, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val systemConfiguration = LocalConfiguration.current
    val configuration = remember(systemConfiguration, language) {
        Configuration(systemConfiguration).apply {
            if (language != AppLanguage.SYSTEM) {
                setLocales(LocaleList(Locale.forLanguageTag(language.tag)))
            }
        }
    }
    val localizedContext = remember(context, configuration) { context.createConfigurationContext(configuration) }
    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalConfiguration provides configuration,
        LocalContext provides localizedContext,
        content = content
    )
}
