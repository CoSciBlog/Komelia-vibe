package snd.komelia.offline.sync.actions

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import snd.komelia.db.TransactionTemplate
import snd.komelia.offline.action.OfflineAction
import snd.komelia.offline.book.repository.OfflineBookMetadataRepository
import snd.komelia.offline.readprogress.OfflineReadProgress
import snd.komelia.offline.readprogress.OfflineReadProgressRepository
import snd.komelia.offline.readprogress.toOfflineReadProgress
import snd.komelia.offline.server.repository.OfflineMediaServerRepository
import snd.komelia.offline.settings.OfflineSettingsRepository
import snd.komelia.offline.sync.model.OfflineLogEntry.Companion.errorLogEntry
import snd.komelia.offline.sync.model.OfflineLogEntry.Companion.infoLogEntry
import snd.komelia.offline.sync.repository.LogJournalRepository
import snd.komelia.offline.tasks.OfflineTaskEmitter
import snd.komelia.offline.user.repository.OfflineUserRepository
import snd.komga.client.book.KomgaBookClient
import snd.komga.client.book.KomgaBookId
import snd.komga.client.book.KomgaBookReadProgressUpdateRequest
import snd.komga.client.book.MediaProfile.DIVINA
import snd.komga.client.book.MediaProfile.EPUB
import snd.komga.client.book.MediaProfile.PDF
import snd.komga.client.book.R2Device
import snd.komga.client.book.R2Progression
import snd.komga.client.user.KomgaUser
import kotlin.time.Clock

class SyncReadProgressAction(
    private val settingsRepository: OfflineSettingsRepository,

    private val bookClient: KomgaBookClient,
    private val bookMetadataRepository: OfflineBookMetadataRepository,
    private val readProgressRepository: OfflineReadProgressRepository,
    private val mediaServerRepository: OfflineMediaServerRepository,
    private val userRepository: OfflineUserRepository,
    private val logJournalRepository: LogJournalRepository,
    private val transactionTemplate: TransactionTemplate,
    private val taskEmitter: OfflineTaskEmitter,
) : OfflineAction {

    suspend fun execute(komgaUser: KomgaUser) {
        val newSyncDate = Clock.System.now()
        val lastSyncDate = settingsRepository.getReadProgressSyncDate().first()
        val deleteReadBooks = settingsRepository.getDeleteReadBooks().first()
        val booksToDelete = mutableListOf<KomgaBookId>()

        transactionTemplate.execute {
            val offlineUser = userRepository.find(komgaUser.id) ?: return@execute
            val server = mediaServerRepository.findByUserId(komgaUser.id) ?: return@execute


            val readProgresses = lastSyncDate
                ?.let { readProgressRepository.findAllModifiedAfter(it, offlineUser.id, server.id) }
                ?: readProgressRepository.findAllByServer(offlineUser.id, server.id)


            for (localProgress in readProgresses) {
                if (deleteReadBooks && syncReadProgress(localProgress)) {
                    booksToDelete += localProgress.bookId
                }

            }
        }

        settingsRepository.putReadProgressSyncDate(newSyncDate)
        booksToDelete.forEach { taskEmitter.deleteBook(it) }
    }

    private suspend fun syncReadProgress(localProgress: OfflineReadProgress): Boolean {
        return try {
            val remoteBook = bookClient.getOne(localProgress.bookId)
            val remoteProgress = remoteBook.readProgress

            if (remoteProgress == null || localProgress.lastModifiedDate > remoteProgress.lastModified) {
                val mediaProfile = remoteBook.media.mediaProfile
                when {
                    mediaProfile == DIVINA || mediaProfile == PDF || remoteBook.media.epubDivinaCompatible ->
                        updateDivinaProgress(remoteBook.id, localProgress)

                    mediaProfile == EPUB -> updateEpubProgress(remoteBook.id, localProgress)
                }

                logJournalRepository.save(
                    infoLogEntry { "Read progress sync ${remoteBook.metadata.title}" }
                )
                localProgress.completed
            } else {
                val locator = if (
                    remoteBook.media.mediaProfile == EPUB &&
                    !remoteBook.media.epubDivinaCompatible
                ) {
                    bookClient.getReadiumProgression(remoteBook.id)?.locator
                } else {
                    null
                }
                readProgressRepository.save(
                    remoteProgress.toOfflineReadProgress(
                        bookId = remoteBook.id,
                        userId = localProgress.userId,
                        locator = locator,
                    )
                )
                remoteProgress.completed
            }

        } catch (e: Exception) {
            val localMetadata = bookMetadataRepository.get(localProgress.bookId)
            logJournalRepository.save(errorLogEntry(e) {
                "Read progress sync error for book ${localMetadata.title} id:${localProgress.bookId}"
            })

            currentCoroutineContext().ensureActive()
            false
        }
    }

    private suspend fun updateDivinaProgress(bookId: KomgaBookId, progress: OfflineReadProgress) {
        val request = when {
            progress.completed -> KomgaBookReadProgressUpdateRequest(completed = true)
            else -> KomgaBookReadProgressUpdateRequest(page = progress.page)
        }

        bookClient.markReadProgress(bookId, request)
    }

    private suspend fun updateEpubProgress(bookId: KomgaBookId, progress: OfflineReadProgress) {
        if (progress.completed || progress.locator == null) {
            bookClient.markReadProgress(bookId, KomgaBookReadProgressUpdateRequest(completed = true))
        } else {
            bookClient.updateReadiumProgression(
                bookId,
                R2Progression(
                    modified = progress.lastModifiedDate,
                    device = R2Device(progress.deviceId, progress.deviceName),
                    locator = progress.locator
                )
            )
        }
    }
}
