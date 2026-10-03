package snd.komelia.api

import snd.komga.client.book.ReadProgress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class RemoteBookApiTest {
    @Test
    fun newerRemoteProgressWinsForDownloadedBook() {
        val local = progress(page = 1, modified = 10)
        val remote = progress(page = 18, modified = 20)

        assertEquals(remote, freshestReadProgress(local, remote))
    }

    @Test
    fun unsyncedLocalProgressWinsWhenItIsNewer() {
        val local = progress(page = 12, modified = 30)
        val remote = progress(page = 8, modified = 20)

        assertEquals(local, freshestReadProgress(local, remote))
    }

    @Test
    fun availableProgressWinsWhenOtherSideHasNone() {
        val local = progress(page = 12, modified = 30)

        assertEquals(local, freshestReadProgress(local, null))
        assertEquals(local, freshestReadProgress(null, local))
        assertNull(freshestReadProgress(null, null))
    }

    private fun progress(page: Int, modified: Long) = ReadProgress(
        page = page,
        completed = false,
        readDate = Instant.fromEpochSeconds(modified),
        deviceId = "device",
        deviceName = "test",
        created = Instant.fromEpochSeconds(1),
        lastModified = Instant.fromEpochSeconds(modified),
    )
}
