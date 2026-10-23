package snd.komelia.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import snd.komelia.settings.model.AppLanguage

@Composable
actual fun AppLanguageEnvironment(language: AppLanguage, content: @Composable () -> Unit) {
    var deviceTag by remember { mutableStateOf(deviceLanguage()) }
    DisposableEffect(Unit) {
        val listener: (org.w3c.dom.events.Event) -> Unit = { deviceTag = deviceLanguage() }
        window.addEventListener("languagechange", listener)
        onDispose { window.removeEventListener("languagechange", listener) }
    }
    val selected = if (language == AppLanguage.SYSTEM) AppLanguage.forDeviceTag(deviceTag) else language
    setResourceLocale(selected.tag)
    CompositionLocalProvider(LocalAppLanguage provides selected, content = content)
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun deviceLanguage(): String = js("navigator.language || 'en'")

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
