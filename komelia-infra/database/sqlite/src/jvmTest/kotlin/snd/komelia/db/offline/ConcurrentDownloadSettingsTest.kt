package snd.komelia.db.offline

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.Database
import snd.komelia.db.OfflineSettings
import snd.komelia.db.SettingsStateWrapper
import snd.komelia.db.migrations.OfflineMigrations
import snd.komelia.db.repository.OfflineSettingsRepositoryWrapper
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConcurrentDownloadSettingsTest {
    @Test
    fun upgradeDefaultsExistingSettingsToTwoAndPreservesOtherChoices(): Unit = runBlocking {
        val url = "jdbc:sqlite:file:download-settings-${UUID.randomUUID()}?mode=memory&cache=shared"
        DriverManager.getConnection(url).use { connection ->
            val migrations = migrationBytes()
            migrations.filterKeys { !it.startsWith("V5__") }.values.forEach { connection.migrate(it) }
            connection.createStatement().use {
                it.executeUpdate("""INSERT INTO SETTINGS (
                    version, is_offline_mode_enabled, download_directory,
                    download_only_unread_series_books, delete_read_books,
                    preload_next_book_enabled, preload_next_book_pages, show_preload_notification
                ) VALUES (1, 0, '/downloads', 1, 1, 1, 7, 1)""")
            }
            connection.migrate(migrations.getValue("V5__concurrent_downloads.sql"))
            val repository = ExposedOfflineSettingsRepository(Database.connect(url, driver = "org.sqlite.JDBC"))
            val upgraded = assertNotNull(repository.get())
            assertEquals(2, upgraded.concurrentDownloads)
            assertTrue(upgraded.downloadOnlyUnreadSeriesBooks)
            assertTrue(upgraded.deleteReadBooks)
            assertTrue(upgraded.preloadNextBookEnabled)
            assertEquals(7, upgraded.preloadNextBookPages)
            assertTrue(upgraded.showPreloadNotification)
            for (count in 1..10) {
                val wrapper = OfflineSettingsRepositoryWrapper(SettingsStateWrapper(assertNotNull(repository.get()), repository::save))
                wrapper.putConcurrentDownloads(count)
                val reopened = OfflineSettingsRepositoryWrapper(SettingsStateWrapper(assertNotNull(repository.get()), repository::save))
                assertEquals(count, reopened.getConcurrentDownloads().first())
            }
            val wrapper = OfflineSettingsRepositoryWrapper(SettingsStateWrapper(assertNotNull(repository.get()), repository::save))
            wrapper.putConcurrentDownloads(0)
            assertEquals(1, assertNotNull(repository.get()).concurrentDownloads)
            wrapper.putConcurrentDownloads(11)
            assertEquals(10, assertNotNull(repository.get()).concurrentDownloads)
            connection.createStatement().use {
                assertFailsWith<SQLException> { it.executeUpdate("UPDATE SETTINGS SET concurrent_downloads = 0") }
                assertFailsWith<SQLException> { it.executeUpdate("UPDATE SETTINGS SET concurrent_downloads = 11") }
            }
        }
    }

    @Test
    fun freshInstallDefaultsToTwo(): Unit = runBlocking {
        val url = "jdbc:sqlite:file:download-settings-new-${UUID.randomUUID()}?mode=memory&cache=shared"
        DriverManager.getConnection(url).use { connection ->
            migrationBytes().values.forEach { connection.migrate(it) }
            val repository = ExposedOfflineSettingsRepository(Database.connect(url, driver = "org.sqlite.JDBC"))
            repository.save(OfflineSettings(downloadDirectory = PlatformFile("/downloads")))
            assertEquals(2, assertNotNull(repository.get()).concurrentDownloads)
        }
    }

    private fun migrationBytes(): Map<String, ByteArray> =
        OfflineMigrations().getResources("V", arrayOf(".sql")).associate { resource ->
            resource.filename to resource.read().use { it.readText().encodeToByteArray() }
        }

    private fun Connection.migrate(bytes: ByteArray) {
        createStatement().use { statement ->
            bytes.decodeToString().split(';').filter { it.isNotBlank() }.forEach { statement.execute(it) }
        }
    }
}
