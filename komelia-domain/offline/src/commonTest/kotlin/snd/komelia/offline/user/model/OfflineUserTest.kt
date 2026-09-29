package snd.komelia.offline.user.model

import snd.komelia.offline.server.model.OfflineMediaServerId
import snd.komga.client.library.KomgaLibraryId
import snd.komga.client.user.AllowExclude.ALLOW_ONLY
import snd.komga.client.user.KomgaAgeRestriction
import snd.komga.client.user.KomgaUser
import snd.komga.client.user.KomgaUserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OfflineUserTest {
    @Test
    fun remoteAccessRestrictionsAreNotCopiedToOfflineUser() {
        val remoteUser = KomgaUser(
            id = KomgaUserId("user-1"),
            email = "reader@example.com",
            roles = setOf("ROLE_USER"),
            sharedAllLibraries = false,
            sharedLibrariesIds = setOf(KomgaLibraryId("available"), KomgaLibraryId("not-imported")),
            labelsAllow = setOf("allowed"),
            labelsExclude = setOf("blocked"),
            ageRestriction = KomgaAgeRestriction(16, ALLOW_ONLY),
        )

        val offlineUser = remoteUser.toOfflineUser(OfflineMediaServerId("server-1"))

        assertEquals(remoteUser.id, offlineUser.id)
        assertEquals(remoteUser.email, offlineUser.email)
        assertEquals(remoteUser.roles, offlineUser.roles)
        assertTrue(offlineUser.sharedAllLibraries)
        assertTrue(offlineUser.sharedLibrariesIds.isEmpty())
        assertTrue(offlineUser.labelsAllow.isEmpty())
        assertTrue(offlineUser.labelsExclude.isEmpty())
        assertNull(offlineUser.ageRestriction)
    }
}
