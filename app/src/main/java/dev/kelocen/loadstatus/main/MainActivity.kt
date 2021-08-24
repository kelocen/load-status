package dev.kelocen.loadstatus.main

import android.app.DownloadManager
import android.app.NotificationChannel
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.button.ButtonState
import dev.kelocen.loadstatus.button.LoadingButton
import dev.kelocen.loadstatus.databinding.ActivityMainBinding
import dev.kelocen.loadstatus.databinding.ContentMainBinding
import dev.kelocen.loadstatus.download.ReceiverDownload
import dev.kelocen.loadstatus.receiver.DownloadReceiver
import dev.kelocen.loadstatus.util.Constants.FILE_NAME_GLIDE
import dev.kelocen.loadstatus.util.Constants.FILE_NAME_LOAD_APP
import dev.kelocen.loadstatus.util.Constants.FILE_NAME_RETROFIT
import dev.kelocen.loadstatus.util.Constants.URL_BUMPTECH_GLIDE
import dev.kelocen.loadstatus.util.Constants.URL_LOAD_APP
import dev.kelocen.loadstatus.util.Constants.URL_RETROFIT
import dev.kelocen.loadstatus.util.LoadUtility
import dev.kelocen.loadstatus.util.createChannel

/**
 * The main activity for the Load Status application.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var activity: ActivityMainBinding
    private lateinit var content: ContentMainBinding
    private lateinit var loadUtil: LoadUtility
    private lateinit var downloadReceiver: DownloadReceiver
    private var name: String? = null
    private var url: String? = null
    private var downloadId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = ActivityMainBinding.inflate(layoutInflater)
        content = activity.contentMain
        setContentView(activity.root)
        setSupportActionBar(activity.toolbar)
        loadUtil = LoadUtility(this)
        downloadReceiver = DownloadReceiver(loadUtil.downloadManager)
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        setupNotificationChannel()
        setupOnCheckedListener()
        setupDownloadButton()
    }

    /**
     * Creates the download [NotificationChannel].
     */
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this,
                          resources.getString(R.string.notification_channel_id),
                          resources.getString(R.string.notification_channel_name))
        }
    }

    /**
     * Configures the [OnCheckedChangeListener] for the radio group and assigns a value to [url].
     */
    private fun setupOnCheckedListener() {
        content.radioGroupItemList.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                content.radioButtonGlide.id -> {
                    url = URL_BUMPTECH_GLIDE
                    name = FILE_NAME_GLIDE
                }
                content.radioButtonLoadApp.id -> {
                    url = URL_LOAD_APP
                    name = FILE_NAME_LOAD_APP
                }
                content.radioButtonRetrofit.id -> {
                    url = URL_RETROFIT
                    name = FILE_NAME_RETROFIT
                }
            }
        }
    }

    /**
     * Configures the [OnClickListener] for the [LoadingButton] and updates available properties
     * for the [ReceiverDownload] object.
     */
    private fun setupDownloadButton() {
        content.customButtonDownload.setOnClickListener {
            if (content.radioGroupItemList.checkedRadioButtonId == -1 || url.isNullOrEmpty()) {
                displayInstructionToast()
            } else {
                content.customButtonDownload.buttonState = ButtonState.Clicked
                downloadId = loadUtil.getDownload(this, url)
                // Pass properties to receiver
                downloadReceiver.name = name
                downloadReceiver.url = url
                downloadReceiver.downloadId = downloadId
            }
        }
    }

    /**
     * Displays a [Toast] that instructs the user to select an item to download.
     */
    private fun displayInstructionToast() {
        Toast.makeText(this,
                       getString(R.string.main_toast_instruct_to_download),
                       Toast.LENGTH_SHORT)
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }
}
