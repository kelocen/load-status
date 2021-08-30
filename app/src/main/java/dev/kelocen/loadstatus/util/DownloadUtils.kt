package dev.kelocen.loadstatus.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.button.ButtonState
import dev.kelocen.loadstatus.main.MainActivity
import dev.kelocen.loadstatus.receiver.DownloadReceiver
import kotlin.properties.Delegates

/**
 * A utility object for downloads.
 */
object LoadUtility {

    /**
     * A [Boolean] by [Delegates.observable] that is updated by [DownloadReceiver] and based on the
     * status of the current download.
     */
    var isComplete: Boolean? by Delegates.observable(null) { _, _, newState ->
        onCompletedListener?.invoke(newState)
    }

    /**
     * A function type used by [MainActivity] to update the [ButtonState] based on the status of
     * the current download.
     */
    var onCompletedListener: ((Boolean?) -> Unit)? = null
    /* Attribution
     * Adopted Content: Syntax for using function types as listeners with observables.
     * Original Source: https://www.kotlindevelopment.com/delegates-observable/
     * Archive: https://web.archive.org/web/20201109005501/https://www.kotlindevelopment.com/delegates-observable/
     * Title: Hassle-free listeners with Observable
     * Author: Andras Kindler
     * Date Published: 7/17/2018
     * Date Retrieved: 8/23/2021
     */

    /**
     * Enqueues a new download with [DownloadManager] and returns the download ID.
     */
    fun getDownload(context: Context, downloadUrl: String?): Long {
        val downloadManager = getDownloadManager(context)
        return downloadManager.enqueue(getRequest(context, downloadUrl))
    }

    /**
     * Returns a [DownloadManager] with the given [Context].
     */
    fun getDownloadManager(context: Context): DownloadManager {
        return context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
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