package dev.kelocen.loadstatus.detail

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.kelocen.loadstatus.databinding.ActivityDetailBinding
import dev.kelocen.loadstatus.databinding.ContentDetailBinding
import dev.kelocen.loadstatus.util.cancelNotifications

/**
 * An [AppCompatActivity] subclass for the detail screen.
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var detail: ActivityDetailBinding
    private lateinit var content: ContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detail = ActivityDetailBinding.inflate(layoutInflater)
        content = detail.contentDetail
        setContentView(detail.root)
        setSupportActionBar(detail.toolbar)
        cancelNotifications()
        setupOkayButton()
    }

    /**
     * Cancels notifications when the notification action button is used to open the detail screen.
     */
    private fun cancelNotifications() {
        val notificationManager =
            ContextCompat.getSystemService(this, NotificationManager::class.java)
        notificationManager?.cancelNotifications()
    }

    /**
     * Configures the OK button for the detail screen.
     */
    private fun setupOkayButton() {
        detail.buttonOk.setOnClickListener {
            onBackPressed()
        }
    }
}
