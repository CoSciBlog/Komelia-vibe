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
            SeriesHomeScreenFilter.Downloaded(order = 4, label = "My offline shelf", pageSize = 60),
            SeriesHomeScreenFilter.RecentlyAdded(order = 1, label = "Recent", pageSize = 10),
        )
        val restored = Json.decodeFromString<List<HomeScreenFilter>>(Json.encodeToString(filters))
        assertEquals(filters, restored)
    }

    @Test
    fun reorderingRetainsDownloadedFilterTypeAndSettings() {
        val filter = SeriesHomeScreenFilter.Downloaded(8, "Downloads", 40)
        assertEquals(filter.copy(order = 2), filter.withOrder(2))
        assertIs<SeriesHomeScreenFilter.Downloaded>(filter.withOrder(2))
    }

    @Test
    fun legacyFiltersKeepTheirExistingSerializedType() {
        val old = """{"type":"io.github.snd_r.komelia.ui.home.SeriesHomeScreenFilter.RecentlyAdded","order":1,"label":"Recent","pageSize":20}"""
        assertEquals(SeriesHomeScreenFilter.RecentlyAdded(1, "Recent", 20),
            Json.decodeFromString<HomeScreenFilter>(old))
    }
}
