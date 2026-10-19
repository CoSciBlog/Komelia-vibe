package snd.komelia.updates

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppVersionTest {
    @Test
    fun releaseTagsAndStoredVersionsHaveTheSameValue() {
        assertEquals(AppVersion(0, 20, 18), AppVersion.fromString("v0.20.18"))
        assertEquals(AppVersion(0, 20, 18), AppVersion.fromString("0.20.18"))
        assertEquals(AppVersion(0, 20, 0), AppVersion.fromString("v0.20"))
        assertTrue(AppVersion.fromString("v0.20.18") > AppVersion.fromString("0.20.9"))
    }

    @Test
    fun serializerKeepsTheExistingStorageFormat() {
        val version = Json.decodeFromString(AppVersionSerializer, "\"v0.20.18\"")
        assertEquals("\"0.20.18\"", Json.encodeToString(AppVersionSerializer, version))
    }

    @Test
    fun malformedTagsStillFail() {
        assertFailsWith<IllegalStateException> { AppVersion.fromString("latest") }
        assertFailsWith<NumberFormatException> { AppVersion.fromString("v0.20.bad") }
    }
}
