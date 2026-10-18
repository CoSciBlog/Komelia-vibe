package snd.komelia.db.offline

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import snd.komga.client.book.KomgaBookId
import java.sql.DriverManager
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadProgressDeletionLookupTest {
    @Test
    fun deletionLookupIncludesEveryUsersProgressOnlyForTheRequestedBook() = runBlocking {
        val url = "jdbc:sqlite:file:deletion-progress-${UUID.randomUUID()}?mode=memory&cache=shared"
        DriverManager.getConnection(url).use {
            val db = Database.connect(url, driver = "org.sqlite.JDBC")
            transaction(db) {
                exec("""CREATE TABLE READ_PROGRESS (
                    book_id TEXT, user_id TEXT, page INTEGER, completed INTEGER,
                    read_date INTEGER, device_id TEXT, device_name TEXT, locator TEXT,
                    created_date INTEGER, last_modified_date INTEGER,
                    PRIMARY KEY(book_id, user_id))""")
                exec("""INSERT INTO READ_PROGRESS VALUES
                    ('book', 'reader', 12, 1, 10, '', '', NULL, 1, 10),
                    ('book', 'other', 3, 0, 20, '', '', NULL, 1, 20),
                    ('unrelated', 'reader', 5, 0, 30, '', '', NULL, 1, 30)""")
            }
            val repository = ExposedOfflineReadProgressRepository(db)
            val rows = repository.findAllByBookId(KomgaBookId("book"))
            assertEquals(setOf("reader", "other"), rows.map { it.userId.value }.toSet())
            assertEquals(setOf(3, 12), rows.map { it.page }.toSet())
            assertEquals(setOf(false, true), rows.map { it.completed }.toSet())
            assertEquals(emptyList(), repository.findAllByBookId(KomgaBookId("missing")))
        }
    }
}
