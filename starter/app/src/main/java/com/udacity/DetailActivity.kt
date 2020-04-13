package com.udacity

import android.app.DownloadManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var downloadedBytesTxt: TextView
    private lateinit var totalBytesTxt: TextView
    private lateinit var downloadStatusTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        downloadedBytesTxt = findViewById(R.id.txt_downloaded_bytes)
        totalBytesTxt = findViewById(R.id.txt_total_bytes)
        downloadStatusTxt = findViewById(R.id.txt_download_status)


         intent?.extras?.let {
             val downloadedBytes = it.getString(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
             val totalBytes = it.getString(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
             val downloadStatus = it.getString(DownloadManager.COLUMN_STATUS)

             downloadedBytesTxt.text = getString(R.string.downloaded_bytes, downloadedBytes)
             downloadStatusTxt.text = getString(R.string.total_bytes, totalBytes)
             downloadStatusTxt.text = getString(R.string.download_status, downloadStatus)
         }

    }

}
