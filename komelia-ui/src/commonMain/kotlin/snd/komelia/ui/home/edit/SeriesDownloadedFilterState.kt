package snd.komelia.ui.home.edit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import snd.komelia.AppNotifications
import snd.komelia.homefilters.SeriesHomeScreenFilter
import snd.komelia.komga.api.model.KomeliaSeries

class SeriesDownloadedFilterState(
    loadSeries: suspend (Int) -> List<KomeliaSeries>,
    notifications: AppNotifications,
    coroutineScope: CoroutineScope,
    initial: SeriesHomeScreenFilter.Downloaded?,
    initialSeries: List<KomeliaSeries>?,
) : SeriesFilterStateType {
    val pageSize = MutableStateFlow((initial?.pageSize ?: 20).coerceAtLeast(1))
    override val series = MutableStateFlow(initialSeries.orEmpty())

    init {
        pageSize.onEach { size ->
            notifications.runCatchingToNotifications {
                series.value = loadSeries(size)
            }
        }.launchIn(coroutineScope)
    }

    fun onPageSizeChange(size: Int) {
        pageSize.value = size.coerceAtLeast(1)
    }
}
