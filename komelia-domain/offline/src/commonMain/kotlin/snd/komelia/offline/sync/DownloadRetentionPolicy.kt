package snd.komelia.offline.sync

fun shouldDownloadSeriesBook(
    downloadOnlyUnread: Boolean,
    completed: Boolean,
): Boolean = !downloadOnlyUnread || !completed

fun shouldDeleteReadDownload(
    deleteReadBooks: Boolean,
    completed: Boolean,
): Boolean = deleteReadBooks && completed

fun shouldPreloadNextBook(
    preloadPages: Int,
    currentPage: Int,
    totalPages: Int,
    isOnline: Boolean,
    isNextBookDownloaded: Boolean,
    isPreloadAlreadyRequested: Boolean,
): Boolean {
    if (preloadPages !in 1..10 || totalPages <= 0 || currentPage !in 1..totalPages) return false
    if (!isOnline || isNextBookDownloaded || isPreloadAlreadyRequested) return false

    return totalPages - currentPage <= preloadPages
}
