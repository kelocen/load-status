package dev.kelocen.loadstatus.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId == id) {
            Toast.makeText(context, "Broadcast & Completed IDs Match!", Toast.LENGTH_SHORT).show()
            val downloadStatus = getDownloadStatus()
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                Toast.makeText(context, "Status: Successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Status: $downloadStatus", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * A helper function for [downloadReceiver] that returns the download status.
 */
private fun getDownloadStatus(): Int {
    val downloadIdQuery = DownloadManager.Query().setFilterById(downloadId)
    val downloadCursor = downloadManager.query(downloadIdQuery)
    return if (downloadCursor.moveToFirst()) {
        val columnIndex = downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        downloadCursor.getInt(columnIndex)
    } else DownloadManager.ERROR_UNKNOWN
}