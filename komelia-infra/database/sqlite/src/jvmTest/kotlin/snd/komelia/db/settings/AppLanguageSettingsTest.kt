package snd.komelia.db.settings

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.Database
import snd.komelia.db.AppSettings
import snd.komelia.db.SettingsStateWrapper
import snd.komelia.db.migrations.AppMigrations
import snd.komelia.db.repository.SettingsRepositoryWrapper
import snd.komelia.settings.model.AppLanguage
import snd.komelia.settings.model.AppTheme
import java.sql.DriverManager
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AppLanguageSettingsTest {
    @Test
    fun automaticLanguageMatchesRegionalDeviceTagsWithoutChangingStoredChoice() {
        val cases = mapOf(
            "de-DE" to AppLanguage.GERMAN, "de_AT" to AppLanguage.GERMAN,
            "en-GB" to AppLanguage.ENGLISH, "es-MX" to AppLanguage.SPANISH,
            "fr-CA" to AppLanguage.FRENCH, "ja-JP" to AppLanguage.JAPANESE,
            "ru-RU" to AppLanguage.RUSSIAN, "zh-Hans-SG" to AppLanguage.CHINESE_SIMPLIFIED,
            "zh_CN" to AppLanguage.CHINESE_SIMPLIFIED, "it-IT" to AppLanguage.ENGLISH,
            "" to AppLanguage.ENGLISH,
        )
        for ((tag, expected) in cases) assertEquals(expected, AppLanguage.forDeviceTag(tag), tag)
        assertEquals(AppLanguage.SYSTEM, AppSettings().appLanguage)
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromTag(""))
    }

    @Test
    fun upgradePreservesSettingsAndLanguageSurvivesReopening(): Unit = runBlocking {
        val url = "jdbc:sqlite:file:app-language-${UUID.randomUUID()}?mode=memory&cache=shared"
        DriverManager.getConnection(url).use { connection ->
            val migrations = AppMigrations().getResources("V", arrayOf(".sql")).associate {
                it.filename to it.read().use { reader -> reader.readText() }
            }
            fun migrate(sql: String) {
                connection.createStatement().use { statement ->
                    sql.split(';').filter { it.isNotBlank() }.forEach { statement.execute(it) }
                }
            }
            migrations.keys.filter { !it.startsWith("V15__") }
                .sortedBy { it.substringAfter('V').substringBefore("__").toInt() }
                .forEach { migrate(migrations.getValue(it)) }
            connection.createStatement().use {
                it.executeUpdate("""INSERT INTO AppSettings (
                    version, username, serverUrl, card_width, series_page_load_size,
                    book_page_load_size, book_list_layout, app_theme, check_for_updates_on_startup,
                    update_last_checked_timestamp, update_last_checked_release_version, update_dismissed_version
                ) VALUES (1, 'reader@example.org', 'https://komga.example.org', 240, 30, 40,
                    'GRID', 'LIGHT', 0, NULL, '0.20.23', '0.20.22')""")
            }
            migrate(migrations.getValue("V15__app_language.sql"))
            val repository = ExposedSettingsRepository(Database.connect(url, driver = "org.sqlite.JDBC"))
            val upgraded = assertNotNull(repository.get())
            assertEquals(AppLanguage.SYSTEM, upgraded.appLanguage)
            assertEquals("reader@example.org", upgraded.username)
            assertEquals("https://komga.example.org", upgraded.serverUrl)
            assertEquals(240, upgraded.cardWidth)
            assertEquals(30, upgraded.seriesPageLoadSize)
            assertEquals(40, upgraded.bookPageLoadSize)
            assertEquals(AppTheme.LIGHT, upgraded.appTheme)
            assertEquals(false, upgraded.checkForUpdatesOnStartup)
            for (language in AppLanguage.entries) {
                val wrapper = SettingsRepositoryWrapper(SettingsStateWrapper(assertNotNull(repository.get()), repository::save))
                wrapper.putAppLanguage(language)
                val reopened = SettingsRepositoryWrapper(SettingsStateWrapper(assertNotNull(repository.get()), repository::save))
                assertEquals(language, reopened.getAppLanguage().first())
                assertEquals(upgraded.copy(appLanguage = language), repository.get())
            }
            connection.createStatement().use { it.executeUpdate("UPDATE AppSettings SET app_language = 'unknown'") }
            assertEquals(AppLanguage.SYSTEM, assertNotNull(repository.get()).appLanguage)
        }
    }

    @Test
    fun oldSerializedSettingsDefaultToSystemAndAllLanguagesRoundTrip() {
        assertEquals(AppLanguage.SYSTEM, Json.decodeFromString<AppSettings>("{}").appLanguage)
        for (language in AppLanguage.entries) {
            val settings = AppSettings(appLanguage = language)
            assertEquals(settings, Json.decodeFromString<AppSettings>(Json.encodeToString(settings)))
            assertEquals(language, AppLanguage.fromTag(language.tag))
        }
    }
}
