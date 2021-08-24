package dev.kelocen.loadstatus.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R


/**
 * A utility class for downloads.
 */
class LoadUtility(context: Context) {

    var downloadManager =
            context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

    /**
     * Enqueues a new download with [DownloadManager] and returns the download ID.
     */
    fun getDownload(context: Context, downloadUrl: String?): Long {
        return downloadManager.enqueue(getRequest(context, downloadUrl))
    }

    /**
     * Returns a [DownloadManager.Request] built from the given [Context] and URL.
     */
    private fun getRequest(context: Context, downloadUrl: String?): DownloadManager.Request {
        return DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(context.getString(R.string.app_name))
                .setDescription(context.getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
    }
}