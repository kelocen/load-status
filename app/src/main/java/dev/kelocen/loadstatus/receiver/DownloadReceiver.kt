package dev.kelocen.loadstatus.receiver

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.download.ReceiverDownload
import dev.kelocen.loadstatus.util.sendNotification
import java.text.SimpleDateFormat
import java.util.*

/**
 * A [BroadcastReceiver] subclass for downloads.
 */
class DownloadReceiver(private var downloadManager: DownloadManager) : BroadcastReceiver() {

    private lateinit var download: ReceiverDownload

    // Properties updated by main activity
    var name: String? = null
    var url: String? = null
    var downloadId: Long = 0

    override fun onReceive(context: Context, intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val notificationManager =
                ContextCompat.getSystemService(context, NotificationManager::class.java)
        if (downloadId == id) {
            val date = getDateToday()
            val sizeKb = (getTotalDownloadSize() / 1024).toDouble()
            val channelID = context.getString(R.string.notification_channel_id)
            val status = when (getDownloadStatus()) {
                DownloadManager.STATUS_SUCCESSFUL -> context.getString(R.string.detail_status_download_successful)
                DownloadManager.STATUS_FAILED -> context.getString(R.string.detail_status_download_failed)
                else -> context.getString(R.string.detail_status_download_unknown_error)
            }
            download = ReceiverDownload(name, url, downloadId, channelID, sizeKb, status, date)
            notificationManager?.sendNotification(String.format(context.getString(R.string.notification_description),
                                                                download.status), context, download)
        }
    }

    /**
     * A helper function for [DownloadReceiver] that returns the download status.
     */
    private fun getDownloadStatus(): Int {
        val queryIdCursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        val columnIndex = queryIdCursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        return if (queryIdCursor.moveToFirst()) {
            queryIdCursor.getInt(columnIndex)
        } else 0
    }

    /**
     * A helper function for [DownloadReceiver] that returns the total download [Byte] size.
     */
    private fun getTotalDownloadSize(): Int {
        val queryIdCursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        val columnIndex = queryIdCursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        return if (queryIdCursor.moveToFirst()) {
            queryIdCursor.getInt(columnIndex)
        } else 0
    }

    /**
     * Returns a [String] that contains today's date.
     */
    private fun getDateToday(): String {
        val dateToday: String
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateToday = dateFormat.format(currentTime)
        return dateToday
    }
}