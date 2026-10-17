package snd.komelia.db.offline.dto

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import snd.komelia.db.offline.tables.OfflineSeriesTable
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

    private fun downloadedIds(db: Database, limit: Int = 20): List<String> {
        val condition = downloadedSeriesCondition()
        return transaction(db) {
            OfflineSeriesTable.select(OfflineSeriesTable.id)
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
                exec("CREATE TABLE SERIES (id TEXT PRIMARY KEY)")
                exec("CREATE TABLE BOOK (id TEXT PRIMARY KEY, series_id TEXT NOT NULL)")
                exec("INSERT INTO SERIES VALUES ('a'), ('b'), ('c'), ('d')")
                exec("INSERT INTO BOOK VALUES ('b1', 'b'), ('b2', 'b'), ('d1', 'd')")
            }
            test(db)
        }
    }
}
