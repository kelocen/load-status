package dev.kelocen.loadstatus.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.kelocen.loadstatus.R
import kotlinx.android.synthetic.main.activity_detail.*

/**
 * An [AppCompatActivity] subclass for the detail screen.
 */
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
    }
}
