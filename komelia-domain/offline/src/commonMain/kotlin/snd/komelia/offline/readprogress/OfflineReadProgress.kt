package snd.komelia.offline.readprogress

import snd.komga.client.book.KomgaBookId
import snd.komga.client.book.R2Locator
import snd.komga.client.book.ReadProgress
import snd.komga.client.user.KomgaUserId
import kotlin.time.Clock
import kotlin.time.Instant

data class OfflineReadProgress(
    val bookId: KomgaBookId,
    val userId: KomgaUserId,
    val page: Int,
    val completed: Boolean,
    val readDate: Instant = Clock.System.now(),
    val deviceId: String = "",
    val deviceName: String = "",
    val locator: R2Locator? = null,
    val createdDate: Instant = Clock.System.now(),
    val lastModifiedDate: Instant = Clock.System.now(),
)

fun ReadProgress.toOfflineReadProgress(
    bookId: KomgaBookId,
    userId: KomgaUserId,
    locator: R2Locator? = null,
) = OfflineReadProgress(
    bookId = bookId,
    userId = userId,
    page = page,
    completed = completed,
    readDate = readDate,
    deviceId = deviceId,
    deviceName = deviceName,
    locator = locator,
    createdDate = created,
    lastModifiedDate = lastModified,
)
