package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val rxPermissions = RxPermissions(this);
    private var downloadID: Long = 0

    private lateinit var radioGroup: RadioGroup
    private lateinit var customButton: LoadingButton
    private var selectedItemUrl: String? = null
    private var selectedItemName: String? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            NotificationUtils.createNotification(this@MainActivity,  selectedItemName!!, downloadId!!)
            customButton.setState(ButtonState.Completed)
        }
    }

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
                    selectedItemName = "GlideRepo.zip"
                }
                R.id.udacity_repo -> {
                    selectedItemUrl =
                            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                    selectedItemName = "UdacityRepo.zip"
                }
                R.id.retrofit_repo -> {
                    selectedItemUrl = "https://github.com/square/retrofit/archive/master.zip"
                    selectedItemName = "RetrofitRepo.zip"
                }
            }
        }

        customButton.setOnClickListener {
            if (!selectedItemUrl.isNullOrEmpty()) {
                this.rxPermissions.requestEachCombined(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).subscribe { permission ->
                    // will emit 1 Permission object
                    if (permission.granted) {
                        // All permissions are granted !
                        download(selectedItemUrl!!, selectedItemName!!)
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // At least one denied permission without ask never again
                        Toast.makeText(
                                this,
                                "Please grant us storage permissions",
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    } else {
                        // At least one denied permission with ask never again Need to go to the settings
                        Toast.makeText(
                                this,
                                "Go to settings to manually grant us denied permissions",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(this, "Please first select an item to download", Toast.LENGTH_SHORT)
                        .show()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun download(url: String, selectedItemName: String) {
        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedItemName)
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
        customButton.setState(ButtonState.Loading)
    }
}
