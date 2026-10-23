package snd.komelia.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import snd.komelia.settings.model.AppLanguage

// A static local invalidates resource consumers without discarding navigation state.
internal val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.SYSTEM }

@Composable
expect fun AppLanguageEnvironment(language: AppLanguage, content: @Composable () -> Unit)
