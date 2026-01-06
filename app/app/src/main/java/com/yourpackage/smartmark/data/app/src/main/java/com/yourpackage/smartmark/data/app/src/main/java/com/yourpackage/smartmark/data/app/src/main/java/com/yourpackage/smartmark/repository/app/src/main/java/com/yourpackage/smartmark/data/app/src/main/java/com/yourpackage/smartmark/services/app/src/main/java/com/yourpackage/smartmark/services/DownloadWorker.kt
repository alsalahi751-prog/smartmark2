package com.yourpackage.smartmark.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.yourpackage.smartmark.MainActivity
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.data.SavedDatabase
import com.yourpackage.smartmark.data.SavedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()
        val fileName = inputData.getString("fileName") ?: extractFileName(url)
        val comment = inputData.getString("comment") ?: ""

        createNotificationChannel()

        val notificationId = System.currentTimeMillis().toInt()
        showProgressNotification(notificationId, fileName)

        return try {
            val file = File(applicationContext.getExternalFilesDir(null), fileName)

            val connection = URL(url).openConnection()
            val input = connection.getInputStream()
            val output = FileOutputStream(file)

            input.copyTo(output)

            output.close()
            input.close()

            Log.d("DownloadWorker", "File saved: ${file.absolutePath}")

            showSuccessNotification(notificationId, fileName)

            saveToDatabase(fileName, url, file.absolutePath, comment)

            Result.success(workDataOf("output" to "Downloaded: $fileName"))
        } catch (e: Exception) {
            Log.e("DownloadWorker", "Failed to download: ${e.message}")
            showFailureNotification(notificationId, fileName)
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "Download Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showProgressNotification(id: Int, fileName: String) {
        val notification = NotificationCompat.Builder(applicationContext, "download_channel")
            .setContentTitle("Downloading...")
            .setContentText(fileName)
            .setSmallIcon(R.drawable.ic_download)
            .setProgress(0, 0, true)
            .build()

        notificationManager.notify(id, notification)
    }

    private fun showSuccessNotification(id: Int, fileName: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, "download_channel")
            .setContentTitle("Download Complete")
            .setContentText("File saved: $fileName")
            .setSmallIcon(R.drawable.ic_download_done)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }

    private fun showFailureNotification(id: Int, fileName: String) {
        val notification = NotificationCompat.Builder(applicationContext, "download_channel")
            .setContentTitle("Download Failed")
            .setContentText("Could not download: $fileName")
            .setSmallIcon(R.drawable.ic_download_failed)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }

    private fun extractFileName(url: String): String {
        return url.substringAfterLast('/').ifEmpty { "downloaded_content" }
    }

    private suspend fun saveToDatabase(title: String, url: String, path: String, comment: String) {
        withContext(Dispatchers.IO) {
            val db = SavedDatabase.getDatabase(applicationContext)
            val dao = db.savedItemDao()

            val type = when {
                title.endsWith(".mp4", true) || url.contains("video") -> "video"
                title.endsWith(".jpg", true) || title.endsWith(".jpeg", true) || title.endsWith(".png", true) -> "image"
                else -> "other"
            }

            val item = SavedItem(
                title = title,
                url = url,
                type = type,
                localPath = path,
                comment = comment
            )
            dao.insert(item)
        }
    }
}
