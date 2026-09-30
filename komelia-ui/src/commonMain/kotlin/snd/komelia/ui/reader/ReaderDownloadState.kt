package snd.komelia.ui.reader

import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.reader_preloading_next_book
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.compose.resources.getString
import snd.komelia.AppNotification
import snd.komelia.AppNotifications
import snd.komelia.komga.api.model.KomeliaBook
import snd.komelia.offline.book.repository.OfflineBookRepository
import snd.komelia.offline.settings.OfflineSettingsRepository
import snd.komelia.offline.sync.PlatformDownloadManager
import snd.komelia.offline.sync.shouldPreloadNextBook
import snd.komelia.offline.tasks.OfflineTaskEmitter
import snd.komelia.offline.tasks.model.HIGH_PRIORITY
import snd.komga.client.book.KomgaBookId

class ReaderDownloadState(
    private val settingsRepository: OfflineSettingsRepository,
    private val bookRepository: OfflineBookRepository,
    private val taskEmitter: OfflineTaskEmitter,
    private val downloadManager: PlatformDownloadManager,
    private val isOffline: StateFlow<Boolean>,
    private val notifications: AppNotifications,
) {
    private val requestedPreloads = mutableSetOf<KomgaBookId>()
    private val preloadMutex = Mutex()

    suspend fun preloadNextBookIfNeeded(
        currentPage: Int,
        totalPages: Int,
        nextBook: KomeliaBook?,
    ) {
        nextBook ?: return
        val preloadPages = settingsRepository.getPreloadNextBookPages().first()
        if (preloadPages == 0) return

        notifications.runCatchingToNotifications {
            preloadMutex.withLock {
                val alreadyRequested = nextBook.id in requestedPreloads
                val alreadyDownloaded = nextBook.downloaded || bookRepository.exists(nextBook.id)
                val isOnline = !isOffline.value && downloadManager.isNetworkAvailable()
                if (!shouldPreloadNextBook(
                        preloadPages = preloadPages,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        isOnline = isOnline,
                        isNextBookDownloaded = alreadyDownloaded,
                        isPreloadAlreadyRequested = alreadyRequested,
                    )
                ) return@withLock

                taskEmitter.downloadBook(nextBook.id, priority = HIGH_PRIORITY)
                requestedPreloads += nextBook.id
                if (settingsRepository.getShowPreloadNotification().first()) {
                    notifications.add(
                        AppNotification.Normal(
                            getString(Res.string.reader_preloading_next_book, nextBook.metadata.title)
                        )
                    )
                }
            }
        }
    }
}
