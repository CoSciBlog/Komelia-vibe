package snd.komelia.offline.settings

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import snd.komga.client.user.KomgaUserId
import kotlin.time.Instant

const val MAX_PRELOAD_NEXT_BOOK_PAGES = 20
const val MIN_PRELOAD_NEXT_BOOK_PAGES = 1
const val DEFAULT_PRELOAD_NEXT_BOOK_PAGES = 3

interface OfflineSettingsRepository {
    fun getOfflineMode(): Flow<Boolean>
    suspend fun putOfflineMode(offline: Boolean)
    fun getUserId(): Flow<KomgaUserId>
    suspend fun putUserId(userId: KomgaUserId)

    fun getReadProgressSyncDate(): Flow<Instant?>
    suspend fun putReadProgressSyncDate(timestamp: Instant)

    fun getDataSyncDate(): Flow<Instant?>
    suspend fun putDataSyncDate(timestamp: Instant)


    fun getDownloadDirectory(): Flow<PlatformFile>
    suspend fun putDownloadDirectory(path: PlatformFile)

    fun getDownloadOnlyUnreadSeriesBooks(): Flow<Boolean>
    suspend fun putDownloadOnlyUnreadSeriesBooks(enabled: Boolean)

    fun getDeleteReadBooks(): Flow<Boolean>
    suspend fun putDeleteReadBooks(enabled: Boolean)

    fun getPreloadNextBookPages(): Flow<Int>
    suspend fun putPreloadNextBookPages(pages: Int)

    fun getPreloadNextBookEnabled(): Flow<Boolean>
    suspend fun putPreloadNextBookEnabled(enabled: Boolean)

    fun getShowPreloadNotification(): Flow<Boolean>
    suspend fun putShowPreloadNotification(enabled: Boolean)
}
