package snd.komelia.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import snd.komelia.settings.model.AppLanguage

@Composable
actual fun AppLanguageEnvironment(language: AppLanguage, content: @Composable () -> Unit) {
    setResourceLocale(language.tag.ifEmpty { null })
    CompositionLocalProvider(LocalAppLanguage provides language, content = content)
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun setResourceLocale(tag: String?): Unit = js("""{
    if (window.__komeliaLocale !== tag) {
        window.__komeliaLocale = tag;
        if (tag) {
            Object.defineProperty(window.navigator, 'languages', {configurable: true, get: () => [tag]});
        } else {
            delete window.navigator.languages;
        }
        window.dispatchEvent(new Event('languagechange'));
    }
}""")
