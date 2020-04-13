package com.udacity

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder

/**
 * Created by rowlandoti on 2020-04-13
 * Last modified 2020-04-13
 */
object NotificationUtils {

    private const val CHANNEL_ID = ".com.udacity.channelId"
    private const val NOTIF_ID = 543210

    fun createNotification(
            ctx: Context,
            fileName: String,
            downloadId: Long
    ) {
        val builder = NotificationCompat.Builder(
                ctx,
                createNotificationChannel(
                        "543210",
                        ctx
                )
        )


        val downloadManager =
                ctx.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

        val q = DownloadManager.Query()
        q.setFilterById(downloadId)

        val cursor: Cursor = downloadManager.query(q)
        cursor.moveToFirst()

        val downloadedBytes: Int =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        val totalBytes: Int =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
        val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

        var status = "Failed"
        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL){
            status = "Success"
        }

        cursor.close()

        val resultIntent = Intent(ctx, DetailActivity::class.java)

        resultIntent.putExtra(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR, downloadedBytes)
        resultIntent.putExtra(DownloadManager.COLUMN_TOTAL_SIZE_BYTES, totalBytes)
        resultIntent.putExtra(DownloadManager.COLUMN_STATUS, status)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(ctx).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val action = NotificationCompat.Action.Builder(
                R.drawable.ic_assistant_black_24dp,
                ctx.getString(R.string.show_download_details),
                pendingIntent
        ).build()

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(fileName)
                .setContentText("Download completed")
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText("Status : $downloadStatus")
                                .setBigContentTitle(fileName)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(action)

        val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIF_ID, builder.build())
    }


    private fun createNotificationChannel(
            channelName: String,
            ctx: Context
    ): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan =
                    NotificationChannel(
                            CHANNEL_ID,
                            channelName,
                            NotificationManager.IMPORTANCE_DEFAULT
                    )
            chan.lightColor = Color.RED
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service =
                    ctx.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }
        return CHANNEL_ID
    }
}