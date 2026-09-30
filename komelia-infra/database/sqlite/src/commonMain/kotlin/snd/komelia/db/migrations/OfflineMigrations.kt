package snd.komelia.db.migrations

import io.github.snd_r.komelia.db.sqlite.sqlite.generated.resources.Res
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class OfflineMigrations : MigrationResourcesProvider() {

    private val migrations = listOf(
        "V1__offline_mode.sql",
        "V2__download_cleanup_settings.sql",
        "V3__reader_preload_settings.sql",
    )

    override suspend fun getMigration(name: String): ByteArray? {
        return try {
            Res.readBytes("files/migrations/offline/$name")
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            null
        }
    }

    override suspend fun getMigrations(): Map<String, ByteArray> {
        return migrations.associateWith { Res.readBytes("files/migrations/offline/$it") }
    }
}
