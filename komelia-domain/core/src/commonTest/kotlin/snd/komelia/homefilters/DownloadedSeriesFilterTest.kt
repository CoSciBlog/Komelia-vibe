package snd.komelia.homefilters

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DownloadedSeriesFilterTest {
    @Test
    fun downloadedFilterSurvivesPersistenceWithCustomLabelOrderAndLimit() {
        val filters: List<HomeScreenFilter> = listOf(
            SeriesHomeScreenFilter.Downloaded(order = 4, label = "My offline shelf", pageSize = 60, onlyUnread = true),
            SeriesHomeScreenFilter.RecentlyAdded(order = 1, label = "Recent", pageSize = 10),
        )
        val restored = Json.decodeFromString<List<HomeScreenFilter>>(Json.encodeToString(filters))
        assertEquals(filters, restored)
    }

    @Test
    fun reorderingRetainsDownloadedFilterTypeAndSettings() {
        val filter = SeriesHomeScreenFilter.Downloaded(8, "Downloads", 40, onlyUnread = true)
        assertEquals(filter.copy(order = 2), filter.withOrder(2))
        assertIs<SeriesHomeScreenFilter.Downloaded>(filter.withOrder(2))
    }

    @Test
    fun oldDownloadedFiltersDefaultToShowingAllSeries() {
        val old = """{"type":"io.github.cosciblog.komelia.vibe.home.SeriesHomeScreenFilter.Downloaded","order":0,"label":"Downloads","pageSize":20}"""
        assertEquals(SeriesHomeScreenFilter.Downloaded(0, "Downloads", 20, onlyUnread = false),
            Json.decodeFromString<HomeScreenFilter>(old))
    }

    @Test
    fun legacyFiltersKeepTheirExistingSerializedType() {
        val old = """{"type":"io.github.snd_r.komelia.ui.home.SeriesHomeScreenFilter.RecentlyAdded","order":1,"label":"Recent","pageSize":20}"""
        assertEquals(SeriesHomeScreenFilter.RecentlyAdded(1, "Recent", 20),
            Json.decodeFromString<HomeScreenFilter>(old))
    }
}
