package dev.kelocen.loadstatus.main

import android.app.DownloadManager
import android.app.NotificationChannel
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
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
    private lateinit var downloadReceiver: DownloadReceiver
    private var name: String? = null
    private var url: String? = null
    private var downloadId: Long = 0
    private var pass: Unit = Unit // Placeholder for empty blocks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = ActivityMainBinding.inflate(layoutInflater)
        content = activity.contentMain
        setContentView(activity.root)
        setSupportActionBar(activity.toolbar)
        downloadReceiver = DownloadReceiver(LoadUtility.getDownloadManager(this))
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        setupNotificationChannel()
        setupOnCheckedListener()
        setupUrlListener()
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
            if (!content.radioButtonCustomUrl.isChecked) {
                content.editCustomUrl.visibility = View.GONE
                content.radioButtonCustomUrl.text = getString(R.string.main_text_custom_url)
            }
            when (checkedId) {
                content.radioButtonGlide.id -> {
                    url = URL_BUMPTECH_GLIDE
                    name = LoadUtility.getDownloadName(URL_BUMPTECH_GLIDE)
                }
                content.radioButtonLoadApp.id -> {
                    url = URL_LOAD_APP
                    name = LoadUtility.getDownloadName(URL_LOAD_APP)
                }
                content.radioButtonRetrofit.id -> {
                    url = URL_RETROFIT
                    name = LoadUtility.getDownloadName(URL_RETROFIT)
                }
                content.radioButtonCustomUrl.id -> {
                    url = null                                      // Clear previous URL
                    content.editCustomUrl.text = null               // Clear previous text
                    content.radioButtonCustomUrl.text = null        // Hide RadioButton text
                    content.editCustomUrl.visibility = View.VISIBLE // Show Edit Text
                }
            }
        }
    }

    /**
     * Configures a text changed listener with a [TextWatcher] object to capture the text from the
     * [EditText] for custom URLs.
     */
    private fun setupUrlListener() {
        content.editCustomUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                pass
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pass
            }

            override fun afterTextChanged(editCustomUrl: Editable?) {
                if (Patterns.WEB_URL.matcher(editCustomUrl.toString()).matches()) {
                    url = editCustomUrl.toString()
                    name = LoadUtility.getDownloadName(editCustomUrl.toString())
                }
            }
        })
    }

    /**
     * Configures the [OnClickListener] for the [LoadingButton] and updates available properties
     * for the [ReceiverDownload] object.
     */
    private fun setupDownloadButton() {
        content.customButtonDownload.setOnClickListener {
            when {
                content.radioGroupItemList.checkedRadioButtonId == -1 -> {
                    displayInstructionToast(getString(R.string.main_toast_instruct_to_download))
                }
                url.isNullOrEmpty() -> {
                    displayInstructionToast(getString(R.string.main_toast_inform_invalid_url))
                }
                content.customButtonDownload.buttonState == ButtonState.Reset -> {
                    content.customButtonDownload.buttonState = ButtonState.Clicked
                    downloadId = LoadUtility.getDownload(this, url, name)
                    downloadReceiver.name = name
                    downloadReceiver.url = url
                    downloadReceiver.downloadId = downloadId
                    LoadUtility.onCompletedListener = { onComplete ->
                        if (onComplete == true) {
                            content.customButtonDownload.buttonState = ButtonState.Completed
                        }
                    }
                }
            }
        }
    }

    /**
     * Displays a [Toast] that instructs the user to select an item or enter a valid Url based on
     * the given [String].
     */
    private fun displayInstructionToast(toastMessage: String) {
        return Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }
}