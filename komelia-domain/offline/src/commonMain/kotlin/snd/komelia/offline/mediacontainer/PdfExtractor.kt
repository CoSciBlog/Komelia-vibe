package snd.komelia.offline.mediacontainer

import io.github.vinceglb.filekit.PlatformFile

interface PdfExtractor {
    fun getPageBytes(
        file: PlatformFile,
        pageIndex: Int,
        width: Int?,
        height: Int?,
    ): ByteArray
}
