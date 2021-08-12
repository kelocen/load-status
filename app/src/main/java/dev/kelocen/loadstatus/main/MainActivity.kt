package dev.kelocen.loadstatus.main

import android.app.DownloadManager
import android.content.IntentFilter
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.databinding.ActivityMainBinding
import dev.kelocen.loadstatus.databinding.ContentMainBinding
import dev.kelocen.loadstatus.download.Download
import dev.kelocen.loadstatus.util.Constants.URL_BUMPTECH_GLIDE
import dev.kelocen.loadstatus.util.Constants.URL_LOAD_APP
import dev.kelocen.loadstatus.util.Constants.URL_RETROFIT
import dev.kelocen.loadstatus.util.downloadReceiver
import dev.kelocen.loadstatus.util.getDownload

/**
 * The main activity for the Load Status application.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var activity: ActivityMainBinding
    private lateinit var content: ContentMainBinding
    private lateinit var download: Download
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = ActivityMainBinding.inflate(layoutInflater)
        content = activity.contentMain
        setContentView(activity.root)
        setSupportActionBar(activity.toolbar)
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        setupOnCheckedListener()
        setupDownloadButton()
    }

    /**
     * Configures the [OnCheckedChangeListener] for the radio group.
     */
    private fun setupOnCheckedListener() {
        content.radioGroupItemList.setOnCheckedChangeListener { _, checkedId ->
            url = when (checkedId) {
                content.radioButtonGlide.id -> URL_BUMPTECH_GLIDE
                content.radioButtonLoadApp.id -> URL_LOAD_APP
                content.radioButtonRetrofit.id -> URL_RETROFIT
                else -> null
            }
        }
    }

    /**
     * Configures the [OnClickListener] for the download button.
     */
    private fun setupDownloadButton() {
        content.customButtonDownload.setOnClickListener {
            if (content.radioGroupItemList.checkedRadioButtonId == -1 || url.isNullOrEmpty()) {
                displayInstructionToast()
            } else {
                download = Download(url, getDownload(this, url))
            }
        }
    }


    /**
     * Displays a toast message that instructs the user to select an item to download.
     */
    private fun displayInstructionToast() {
        Toast.makeText(this, getString(R.string.instruct_to_download), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }
}
