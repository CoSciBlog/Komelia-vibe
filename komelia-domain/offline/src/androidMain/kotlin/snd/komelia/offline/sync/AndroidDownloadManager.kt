package snd.komelia.offline.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Data
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import snd.komga.client.book.KomgaBookId

/**
 *  recommended way to manage long-running download tasks
 *  should be able to execute and reschedule the work even if app is killed in background
 *  https://developer.android.com/reference/androidx/work/WorkManager
 */
class AndroidDownloadManager(
    private val context: Context,
) : PlatformDownloadManager {

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun launchBookDownload(bookId: KomgaBookId) {

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(
                Data.Builder()
                    .putString(bookIdDataKey, bookId.value)
                    .build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(bookId.value, ExistingWorkPolicy.KEEP, request)
    }

    override suspend fun cancelBookDownload(bookId: KomgaBookId) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(bookId.value)
    }
}
