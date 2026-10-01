package snd.komelia.ui.reader.image

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReaderPageSelectionTest {
    @Test
    fun missingOldBookPageFallsBackToFirstSpread() {
        assertEquals(0, resolveSpreadIndex(spreadCount = 18, matchedIndex = -1))
    }

    @Test
    fun validSpreadIndexIsPreserved() {
        assertEquals(7, resolveSpreadIndex(spreadCount = 18, matchedIndex = 7))
    }

    @Test
    fun emptySpreadListDoesNotProduceAnIndex() {
        assertNull(resolveSpreadIndex(spreadCount = 0, matchedIndex = -1))
    }

    @Test
    fun panelPageIsClampedToAvailablePages() {
        assertEquals(0, resolvePageIndex(pageCount = 18, requestedPage = 0))
        assertEquals(17, resolvePageIndex(pageCount = 18, requestedPage = 23))
        assertNull(resolvePageIndex(pageCount = 0, requestedPage = 1))
    }
}
