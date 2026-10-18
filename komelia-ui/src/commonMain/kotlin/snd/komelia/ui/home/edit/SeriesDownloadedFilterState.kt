package snd.komelia.ui.home.edit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import snd.komelia.AppNotifications
import snd.komelia.homefilters.SeriesHomeScreenFilter
import snd.komelia.komga.api.model.KomeliaSeries

class SeriesDownloadedFilterState(
    loadSeries: suspend (Int, Boolean) -> List<KomeliaSeries>,
    notifications: AppNotifications,
    coroutineScope: CoroutineScope,
    initial: SeriesHomeScreenFilter.Downloaded?,
    initialSeries: List<KomeliaSeries>?,
) : SeriesFilterStateType {
    val pageSize = MutableStateFlow((initial?.pageSize ?: 20).coerceAtLeast(1))
    val onlyUnread = MutableStateFlow(initial?.onlyUnread ?: false)
    override val series = MutableStateFlow(initialSeries.orEmpty())

    init {
        combine(pageSize, onlyUnread) { size, unread -> size to unread }.onEach { (size, unread) ->
            notifications.runCatchingToNotifications {
                series.value = loadSeries(size, unread)
            }
        }.launchIn(coroutineScope)
    }

    fun onPageSizeChange(size: Int) {
        pageSize.value = size.coerceAtLeast(1)
    }

    fun onOnlyUnreadChange(onlyUnread: Boolean) {
        this.onlyUnread.value = onlyUnread
    }
}
