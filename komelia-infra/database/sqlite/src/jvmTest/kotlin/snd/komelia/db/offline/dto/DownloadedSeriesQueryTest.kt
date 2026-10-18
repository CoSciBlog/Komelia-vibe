package snd.komelia.db.offline.dto

import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import snd.komelia.db.offline.tables.OfflineSeriesTable
import snd.komelia.db.offline.tables.OfflineReadProgressSeriesTable
import java.sql.DriverManager
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class DownloadedSeriesQueryTest {
    @Test
    fun metadataOnlySeriesAreExcludedAndMultipleBooksProduceOneSeries() = withDatabase { db ->
        assertEquals(listOf("b", "d"), downloadedIds(db))
    }

    @Test
    fun downloadFilteringHappensBeforeLimit() = withDatabase { db ->
        assertEquals(listOf("b"), downloadedIds(db, 1))
        assertEquals(listOf("b", "d"), downloadedIds(db, 2))
    }

    @Test
    fun deletingLastLocalBookRemovesSeriesButRetainsOtherDownloadedSeries() = withDatabase { db ->
        transaction(db) { exec("DELETE FROM BOOK WHERE series_id = 'b'") }
        assertEquals(listOf("d"), downloadedIds(db))
        transaction(db) { exec("DELETE FROM BOOK") }
        assertEquals(emptyList(), downloadedIds(db))
    }

    @Test
    fun unreadFilterRetainsUnstartedAndInProgressButHidesCompletedSeries() = withDatabase { db ->
        transaction(db) {
            exec("INSERT INTO BOOK VALUES ('a1', 'a'), ('c1', 'c')")
            exec("INSERT INTO READ_PROGRESS_SERIES VALUES ('b', 'reader', 2, 0), ('c', 'reader', 0, 1), ('d', 'reader', 1, 1)")
        }
        assertEquals(listOf("a", "c", "d"), downloadedIds(db, onlyUnread = true))
        assertEquals(listOf("a", "b", "c", "d"), downloadedIds(db))
    }

    @Test
    fun completedSeriesAreFilteredBeforeLimitAndUseOnlyActiveUserProgress() = withDatabase { db ->
        transaction(db) {
            exec("INSERT INTO READ_PROGRESS_SERIES VALUES ('b', 'reader', 2, 0), ('d', 'other', 2, 0)")
        }
        assertEquals(listOf("d"), downloadedIds(db, limit = 1, onlyUnread = true))
        assertEquals(listOf("b"), downloadedIds(db, onlyUnread = true, userId = "other"))
        transaction(db) { exec("UPDATE READ_PROGRESS_SERIES SET read_count = 2, in_progress_count = 0 WHERE series_id = 'd'") }
        assertEquals(listOf("d"), downloadedIds(db, onlyUnread = true))
        transaction(db) { exec("INSERT INTO READ_PROGRESS_SERIES VALUES ('d', 'reader', 2, 0)") }
        assertEquals(emptyList(), downloadedIds(db, onlyUnread = true))
        transaction(db) { exec("DELETE FROM READ_PROGRESS_SERIES WHERE series_id = 'b' AND user_id = 'reader'") }
        assertEquals(listOf("b"), downloadedIds(db, onlyUnread = true))
    }

    private fun downloadedIds(db: Database, limit: Int = 20, onlyUnread: Boolean = false, userId: String = "reader"): List<String> {
        val condition = downloadedSeriesCondition(onlyUnread)
        return transaction(db) {
            OfflineSeriesTable.join(
                OfflineReadProgressSeriesTable, JoinType.LEFT,
                OfflineSeriesTable.id, OfflineReadProgressSeriesTable.seriesId,
                additionalConstraint = { OfflineReadProgressSeriesTable.userId eq userId },
            ).select(OfflineSeriesTable.id)
                .where { condition }
                .orderBy(OfflineSeriesTable.id)
                .limit(limit)
                .map { it[OfflineSeriesTable.id] }
        }
    }

    private fun withDatabase(test: (Database) -> Unit) {
        // Keep the shared in-memory database alive while Exposed opens transaction connections.
        val url = "jdbc:sqlite:file:downloaded-series-${UUID.randomUUID()}?mode=memory&cache=shared"
        DriverManager.getConnection(url).use {
            val db = Database.connect(url, driver = "org.sqlite.JDBC")
            transaction(db) {
                exec("CREATE TABLE SERIES (id TEXT PRIMARY KEY, books_count INTEGER NOT NULL)")
                exec("CREATE TABLE BOOK (id TEXT PRIMARY KEY, series_id TEXT NOT NULL)")
                exec("CREATE TABLE READ_PROGRESS_SERIES (series_id TEXT, user_id TEXT, read_count INTEGER, in_progress_count INTEGER, PRIMARY KEY (series_id, user_id))")
                exec("INSERT INTO SERIES VALUES ('a', 2), ('b', 2), ('c', 2), ('d', 2)")
                exec("INSERT INTO BOOK VALUES ('b1', 'b'), ('b2', 'b'), ('d1', 'd')")
            }
            test(db)
        }
    }
}
