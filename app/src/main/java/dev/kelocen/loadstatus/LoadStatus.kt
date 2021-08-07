@file: Suppress("unused")

package dev.kelocen.loadstatus

import android.app.Application
import timber.log.Timber

/**
 * An [Application] subclass for the DownloadStatus application.
 */
class LoadStatus: Application() {

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}