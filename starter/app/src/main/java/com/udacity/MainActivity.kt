package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var radioGroup: RadioGroup
    private lateinit var customButton: LoadingButton
    private var selectedItemUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        radioGroup = findViewById(R.id.radio_group)
        customButton = findViewById(R.id.custom_button)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.glid_repo -> {
                    selectedItemUrl = "https://github.com/bumptech/glide/archive/master.zip"
                }
                R.id.udacity_repo -> {
                    selectedItemUrl =
                            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                }
                R.id.retrofit_repo -> {
                    selectedItemUrl = "https://github.com/square/retrofit/archive/master.zip"
                }
            }
        }

        customButton.setOnClickListener {
            if (!selectedItemUrl.isNullOrEmpty()) {
                download(selectedItemUrl!!)
            } else {
                Toast.makeText(this, "Please first select an item to download", Toast.LENGTH_SHORT)
                        .show()
            }
        }


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(url: String) {
        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

}
