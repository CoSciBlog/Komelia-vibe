package snd.komelia.db

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.serialization.Serializable
import snd.komelia.offline.server.model.OfflineMediaServerId
import snd.komelia.offline.user.model.OfflineUser
import snd.komelia.offline.settings.DEFAULT_PRELOAD_NEXT_BOOK_PAGES
import snd.komga.client.user.KomgaUserId
import kotlin.time.Instant

@Serializable
data class OfflineSettings(
    val isOfflineModeEnabled: Boolean = false,
    val downloadDirectory: PlatformFile ,
    val userId: KomgaUserId = OfflineUser.ROOT,
    val serverId: OfflineMediaServerId? = null,
    val readProgressSyncDate: Instant? = null,
    val dataSyncDate: Instant? = null,
    val downloadOnlyUnreadSeriesBooks: Boolean = false,
    val deleteReadBooks: Boolean = false,
    val preloadNextBookPages: Int = DEFAULT_PRELOAD_NEXT_BOOK_PAGES,
    val preloadNextBookEnabled: Boolean = false,
    val showPreloadNotification: Boolean = false,
)
