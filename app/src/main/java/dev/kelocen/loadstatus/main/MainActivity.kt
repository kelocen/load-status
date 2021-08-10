package dev.kelocen.loadstatus.main

import android.app.DownloadManager
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.receiver.DownloadReceiver
import dev.kelocen.loadstatus.util.getDownload
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/**
 * The main activity for the Load Status application.
 */
class MainActivity : AppCompatActivity() {

    private var receiver = DownloadReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button_download.setOnClickListener {
            if (radio_group_item_list.checkedRadioButtonId == -1) {
                displayInstructionToast()
            } else {
                getDownload(this)
            }
        }
    }

    /**
     * Displays a toast message that instructs the user to select an item to download.
     */
    private fun displayInstructionToast() {
        Toast.makeText(this, getString(R.string.instruct_to_download), Toast.LENGTH_SHORT).show()
    }
}
