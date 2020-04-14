package com.udacity

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var motionLayout: MotionLayout
    private lateinit var downloadedBytesTxt: TextView
    private lateinit var totalBytesTxt: TextView
    private lateinit var downloadStatusTxt: TextView
    private lateinit var btnNavMain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        btnNavMain = findViewById(R.id.btn_nav_main)
        downloadedBytesTxt = findViewById(R.id.txt_file_name)
        totalBytesTxt = findViewById(R.id.txt_total_bytes)
        downloadStatusTxt = findViewById(R.id.txt_download_status)
        motionLayout = findViewById(R.id.mnt_detail)


         intent?.extras?.let {
             val downloadFileName = it.getString(DownloadManager.COLUMN_TITLE)
             val downloadedBytes = it.getInt(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
             val totalBytes = it.getInt(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
             val downloadStatus = it.getString(DownloadManager.COLUMN_STATUS)

             downloadedBytesTxt.text = getString(R.string.file_name, downloadFileName)
             totalBytesTxt.text = getString(R.string.downloaded_bytes, downloadedBytes, totalBytes)
             downloadStatusTxt.text = getString(R.string.download_status, downloadStatus)
         }

        btnNavMain.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }

}
