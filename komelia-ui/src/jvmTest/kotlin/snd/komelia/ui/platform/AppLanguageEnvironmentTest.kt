package snd.komelia.ui.platform

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.settings_appearance_title
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.settings_updates_check
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.series_books_count
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.pluralStringResource
import snd.komelia.settings.model.AppLanguage
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppLanguageEnvironmentTest {
    @Test
    fun switchingLanguagesRefreshesExistingStringsAndPreservesRememberedState() = runTest {
        val originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.GERMANY)
        val language = mutableStateOf(AppLanguage.SYSTEM)
        val clock = object : MonotonicFrameClock {
            override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R =
                onFrame(testScheduler.currentTime * 1_000_000)
        }
        val recomposer = Recomposer(coroutineContext + clock)
        val composition = Composition(object : AbstractApplier<Unit>(Unit) {
            override fun insertTopDown(index: Int, instance: Unit) = Unit
            override fun insertBottomUp(index: Int, instance: Unit) = Unit
            override fun remove(index: Int, count: Int) = Unit
            override fun move(from: Int, to: Int, count: Int) = Unit
            override fun onClear() = Unit
        }, recomposer)
        val job = backgroundScope.launch(clock) { recomposer.runRecomposeAndApplyChanges() }
        var title = ""
        var checkUpdates = ""
        var remembered: Any? = null
        var bookCount = ""
        val count = mutableStateOf(2)
        try {
            composition.setContent {
                CompositionLocalProvider(LocalDensity provides Density(1f)) {
                    AppLanguageEnvironment(language.value) {
                        title = stringResource(Res.string.settings_appearance_title)
                        checkUpdates = stringResource(Res.string.settings_updates_check)
                        bookCount = pluralStringResource(Res.plurals.series_books_count, count.value, count.value)
                        remembered = remember { Any() }
                    }
                }
            }
            runCurrent()
            val initialState = remembered
            assertEquals("Darstellung", title)
            language.value = AppLanguage.ENGLISH
            Snapshot.sendApplyNotifications()
            runCurrent()
            assertEquals("Appearance", title)
            assertEquals("Check for updates", checkUpdates)
            assertSame(initialState, remembered)
            language.value = AppLanguage.GERMAN
            Snapshot.sendApplyNotifications()
            runCurrent()
            assertEquals("Darstellung", title)
            assertEquals("Nach Updates suchen", checkUpdates)
            language.value = AppLanguage.CHINESE_SIMPLIFIED
            Snapshot.sendApplyNotifications()
            runCurrent()
            assertEquals("外观", title)
            val titles = mapOf(
                AppLanguage.SPANISH to "Apariencia",
                AppLanguage.FRENCH to "Apparence",
                AppLanguage.JAPANESE to "外観",
                AppLanguage.RUSSIAN to "Оформление",
            )
            for ((selected, expected) in titles) {
                language.value = selected
                Snapshot.sendApplyNotifications()
                runCurrent()
                assertEquals(expected, title)
                assertSame(initialState, remembered)
            }
            assertEquals("2 книги", bookCount)
            count.value = 5
            Snapshot.sendApplyNotifications()
            runCurrent()
            assertEquals("5 книг", bookCount)
            count.value = 21
            Snapshot.sendApplyNotifications()
            runCurrent()
            assertEquals("21 книга", bookCount)
            language.value = AppLanguage.SYSTEM
            Snapshot.sendApplyNotifications()
            runCurrent()
            assertEquals("Darstellung", title)
            assertSame(initialState, remembered)
        } finally {
            composition.dispose()
            recomposer.close()
            job.cancel()
            Locale.setDefault(originalLocale)
        }
    }
}
