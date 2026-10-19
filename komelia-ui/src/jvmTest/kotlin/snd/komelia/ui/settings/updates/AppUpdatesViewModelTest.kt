package snd.komelia.ui.settings.updates

import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import snd.komelia.AppNotifications
import snd.komelia.settings.CommonSettingsRepository
import snd.komelia.ui.LoadState
import snd.komelia.updates.AppRelease
import snd.komelia.updates.AppUpdater
import snd.komelia.updates.UpdateProgress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppUpdatesViewModelTest {
    @Test
    fun failedCheckStopsLoadingAndCanBeRetriedWithAnEmptyResponse() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var attempts = 0
            var timestampsSaved = 0
            val updater = object : AppUpdater {
                override suspend fun getReleases(): List<AppRelease> {
                    attempts++
                    if (attempts == 1) error("For input string: v0")
                    return emptyList()
                }
                override suspend fun updateToLatest(): Flow<UpdateProgress>? = null
                override fun updateTo(release: AppRelease): Flow<UpdateProgress>? = null
            }
            val settings = Proxy.newProxyInstance(
                CommonSettingsRepository::class.java.classLoader,
                arrayOf(CommonSettingsRepository::class.java),
            ) { _, method, _ ->
                check(method.name == "putLastUpdateCheckTimestamp")
                timestampsSaved++
                Unit
            } as CommonSettingsRepository
            val model = AppUpdatesViewModel(MutableStateFlow(emptyList()), updater, settings, AppNotifications())
            model.checkForUpdates()
            model.checkForUpdates()
            runCurrent()
            assertIs<LoadState.Error>(model.state.value)
            assertEquals(1, attempts)
            assertEquals(0, timestampsSaved)
            model.checkForUpdates()
            runCurrent()
            assertIs<LoadState.Success<Unit>>(model.state.value)
            assertEquals(2, attempts)
            assertEquals(1, timestampsSaved)
            model.onDispose()
        } finally {
            Dispatchers.resetMain()
        }
    }
}
