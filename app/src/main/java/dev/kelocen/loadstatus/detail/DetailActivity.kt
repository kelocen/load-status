package dev.kelocen.loadstatus.detail

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.databinding.ActivityDetailBinding
import dev.kelocen.loadstatus.databinding.ContentDetailBinding
import dev.kelocen.loadstatus.download.ReceiverDownload
import dev.kelocen.loadstatus.util.Constants.DOWNLOAD
import dev.kelocen.loadstatus.util.Constants.DOWNLOAD_BUNDLE
import dev.kelocen.loadstatus.util.cancelNotifications

/**
 * An [AppCompatActivity] subclass for the detail screen.
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var detail: ActivityDetailBinding
    private lateinit var content: ContentDetailBinding
    private lateinit var motionLayout: MotionLayout
    private val pass: Unit = Unit  // Placeholder for empty blocks

    /**
     * A [MotionLayout.TransitionListener] object that monitors the state of the motion
     * scene transitions.
     *
     * This listener is responsible for activating the second transition of the motion scene after
     * the first transition has completed.
     */
    private var motionListener = object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
        ) {
            pass
        }

        override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float,
        ) {
            pass
        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            if (currentId == R.id.labels_end) {
                motionLayout?.setTransition(R.id.transition_download_details)
                motionLayout?.transitionToEnd()
            }
        }

        override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float,
        ) {
            pass
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detail = ActivityDetailBinding.inflate(layoutInflater)
        content = detail.contentDetail
        motionLayout = content.motionLayoutContentDetail
        motionLayout.addTransitionListener(motionListener)
        setContentView(detail.root)
        setSupportActionBar(detail.toolbar)
        cancelNotifications()
        setupOkButton()
        populateTextFields()
    }

    /**
     * Assigns each [ReceiverDownload] property a corresponding [TextView].
     */
    private fun populateTextFields() {
        val bundle = intent?.getBundleExtra(DOWNLOAD_BUNDLE)
        val download = bundle?.getParcelable<ReceiverDownload>(DOWNLOAD)
        if (download?.status.equals(getString(R.string.detail_status_download_failed)) ||
            download?.status.equals(getString(R.string.detail_status_download_unknown_error))) {
            content.textDownloadStatus.setTextColor(Color.RED)
        } else {
            content.textDownloadStatus.setTextColor(Color.GREEN)
        }
        content.textDownloadStatus.text = download?.status
        content.textDownloadDate.text = download?.date
        content.textDownloadSize.text =
                String.format(getString(R.string.detail_text_download_size), download?.sizeKb)
        content.textDownloadName.text = download?.name
        content.textDownloadUrl.text = download?.url
    }

    /**
     * Cancels notifications when [DetailActivity] is created.
     *
     * This method insures that notifications will be canceled when the notification action
     * button is used to open the activity.
     */
    private fun cancelNotifications() {
        val notificationManager =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
        notificationManager?.cancelNotifications()
    }

    /**
     * Configures the OK button for the detail screen.
     */
    private fun setupOkButton() {
        detail.buttonOk.setOnClickListener {
            finish()
        }
    }
}