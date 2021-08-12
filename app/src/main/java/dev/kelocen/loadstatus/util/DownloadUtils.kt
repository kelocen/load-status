package dev.kelocen.loadstatus.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R

private lateinit var downloadManager: DownloadManager
private var downloadId: Long = 0

/**
 * Enqueues a new download with [DownloadManager] and returns the download ID.
 */
fun getDownload(context: Context, downloadUrl: String?): Long {
    val request =
        DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle(context.getString(R.string.app_name))
            .setDescription(context.getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

    downloadManager =
        context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    downloadId = downloadManager.enqueue(request)
    return downloadId
}

/**
 * A [BroadcastReceiver] object for downloads.
 */
var downloadReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
    }
}