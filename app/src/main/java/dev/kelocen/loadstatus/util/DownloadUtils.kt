package dev.kelocen.loadstatus.util

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.kelocen.loadstatus.R

private lateinit var downloadManager: DownloadManager
private var downloadId: Long = 0

/**
 * Enqueues a new download with [DownloadManager] and returns the download ID.
 */
fun getDownload(context: Context, downloadUrl: String?): Long {
    downloadManager =
        context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    downloadId = downloadManager.enqueue(getRequest(context, downloadUrl))
    return downloadId
}

/**
 * Helper function for [getDownload] that returns a [DownloadManager.Request] built from the given [Context] and URL.
 */
fun getRequest(context: Context, downloadUrl: String?): DownloadManager.Request {
    return DownloadManager.Request(Uri.parse(downloadUrl))
        .setTitle(context.getString(R.string.app_name))
        .setDescription(context.getString(R.string.app_description))
        .setRequiresCharging(false)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
}

/**
 * A [BroadcastReceiver] object for downloads.
 */
var downloadReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        if (downloadId == id) {
            val downloadStatus = getDownloadStatus()
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                notificationManager?.sendNotification(
                        context.getString(R.string.notification_description), context)
            } else {
                notificationManager?.sendNotification(
                        context.getString(R.string.notification_error), context)
            }
        }
    }
}

/**
 * A helper function for [downloadReceiver] that returns the download status.
 */
private fun getDownloadStatus(): Int {
    val queryIdCursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
    return if (queryIdCursor.moveToFirst()) {
        val columnIndex = queryIdCursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        queryIdCursor.getInt(columnIndex)
    } else DownloadManager.ERROR_UNKNOWN
}