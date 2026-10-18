package snd.komelia.ui.home.edit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import snd.komelia.AppNotifications
import snd.komelia.homefilters.SeriesHomeScreenFilter
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SeriesDownloadedFilterStateTest {
    @Test
    fun savedFilterLoadsLocalPreviewImmediatelyAndUsesEditedLimit() = runTest {
        val requestedSizes = mutableListOf<Int>()
        val state = SeriesDownloadedFilterState(
            loadSeries = { size, _ -> requestedSizes += size; emptyList() },
            notifications = AppNotifications(),
            coroutineScope = backgroundScope,
            initial = SeriesHomeScreenFilter.Downloaded(4, "My downloads", 40),
            initialSeries = emptyList(),
        )
        runCurrent()
        assertEquals(listOf(40), requestedSizes)
        state.onPageSizeChange(60)
        runCurrent()
        assertEquals(listOf(40, 60), requestedSizes)
        state.onPageSizeChange(0)
        runCurrent()
        assertEquals(listOf(40, 60, 1), requestedSizes)
    }

    @Test
    fun savedUnreadSelectionReloadsPreviewWhenToggled() = runTest {
        val requests = mutableListOf<Pair<Int, Boolean>>()
        val state = SeriesDownloadedFilterState(
            loadSeries = { size, unread -> requests += size to unread; emptyList() },
            notifications = AppNotifications(),
            coroutineScope = backgroundScope,
            initial = SeriesHomeScreenFilter.Downloaded(0, "Downloads", 40, onlyUnread = true),
            initialSeries = emptyList(),
        )
        runCurrent()
        assertEquals(true, state.onlyUnread.value)
        state.onOnlyUnreadChange(false)
        runCurrent()
        state.onPageSizeChange(10)
        runCurrent()
        state.onOnlyUnreadChange(true)
        runCurrent()
        assertEquals(listOf(40 to true, 40 to false, 10 to false, 10 to true), requests)
    }

    @Test
    fun unavailableLocalDownloadsGiveAnEmptyPreview() = runTest {
        val state = SeriesDownloadedFilterState(
            loadSeries = { _, _ -> emptyList() },
            notifications = AppNotifications(),
            coroutineScope = backgroundScope,
            initial = null,
            initialSeries = null,
        )
        runCurrent()
        assertEquals(20, state.pageSize.value)
        assertEquals(false, state.onlyUnread.value)
        assertEquals(emptyList(), state.series.value)
    }
}
