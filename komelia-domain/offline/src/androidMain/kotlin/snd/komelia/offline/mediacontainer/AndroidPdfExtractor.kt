package snd.komelia.offline.mediacontainer

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.context
import java.io.ByteArrayOutputStream

class AndroidPdfExtractor : PdfExtractor {
    private val renderLock = Any()

    override fun getPageBytes(
        file: PlatformFile,
        pageIndex: Int,
        width: Int?,
        height: Int?,
    ): ByteArray = synchronized(renderLock) {
        openFileDescriptor(file).use { descriptor ->
            PdfRenderer(descriptor).use { renderer ->
                require(pageIndex in 0 until renderer.pageCount) {
                    "PDF page index $pageIndex is out of bounds for ${renderer.pageCount} pages"
                }

                renderer.openPage(pageIndex).use { page ->
                    val requestedWidth = width?.takeIf { it > 0 }
                    val requestedHeight = height?.takeIf { it > 0 }
                    val targetWidth = if (requestedWidth != null && requestedHeight != null) {
                        requestedWidth
                    } else {
                        page.width
                    }
                    val targetHeight = if (requestedWidth != null && requestedHeight != null) {
                        requestedHeight
                    } else {
                        page.height
                    }
                    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                    try {
                        bitmap.eraseColor(Color.WHITE)
                        val transform = Matrix().apply {
                            setScale(
                                targetWidth.toFloat() / page.width,
                                targetHeight.toFloat() / page.height,
                            )
                        }
                        page.render(bitmap, null, transform, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                        ByteArrayOutputStream().use { output ->
                            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                                "Could not encode rendered PDF page"
                            }
                            output.toByteArray()
                        }
                    } finally {
                        bitmap.recycle()
                    }
                }
            }
        }
    }

    private fun openFileDescriptor(file: PlatformFile): ParcelFileDescriptor {
        return when (val androidFile = file.androidFile) {
            is AndroidFile.FileWrapper -> ParcelFileDescriptor.open(
                androidFile.file,
                ParcelFileDescriptor.MODE_READ_ONLY,
            )

            is AndroidFile.UriWrapper -> FileKit.context.contentResolver
                .openFileDescriptor(androidFile.uri, "r")
                ?: error("Failed to open PDF file descriptor ${androidFile.uri}")
        }
    }
}
