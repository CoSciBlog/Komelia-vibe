package snd.komelia.offline.sync

fun shouldDownloadSeriesBook(
    downloadOnlyUnread: Boolean,
    completed: Boolean,
): Boolean = !downloadOnlyUnread || !completed

fun shouldDeleteReadDownload(
    deleteReadBooks: Boolean,
    completed: Boolean,
): Boolean = deleteReadBooks && completed
