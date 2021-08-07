package dev.kelocen.loadstatus.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.download.Download

/**
 * TODO
 */
fun getDownload(context: Context) {
    val download = Download()
    val request =
        DownloadManager.Request(Uri.parse(download.url))
            .setTitle(context.getString(R.string.app_name))
            .setDescription(context.getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

    val downloadManager = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    download.downloadId = downloadManager.enqueue(request) // enqueue puts the download request in the queue.
}