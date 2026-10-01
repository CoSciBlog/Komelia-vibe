package snd.komelia.ui.reader.image

internal fun resolveSpreadIndex(
    spreadCount: Int,
    matchedIndex: Int,
): Int? = when {
    spreadCount <= 0 -> null
    matchedIndex in 0 until spreadCount -> matchedIndex
    else -> 0
}

internal fun resolvePageIndex(
    pageCount: Int,
    requestedPage: Int,
): Int? {
    if (pageCount <= 0) return null
    return (requestedPage - 1).coerceIn(0, pageCount - 1)
}
