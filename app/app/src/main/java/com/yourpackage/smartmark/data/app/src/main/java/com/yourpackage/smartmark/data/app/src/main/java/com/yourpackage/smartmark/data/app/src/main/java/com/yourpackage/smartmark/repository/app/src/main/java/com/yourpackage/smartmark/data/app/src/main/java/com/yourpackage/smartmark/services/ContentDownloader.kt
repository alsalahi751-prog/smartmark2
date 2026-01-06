package com.yourpackage.smartmark.services

import android.content.Context
import androidx.work.*

object ContentDownloader {

    fun downloadContentWithDetails(context: Context, url: String, title: String, comment: String) {
        if (isValidDirectUrl(url)) {
            val fileName = title.ifEmpty { extractFileName(url) }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(
                    workDataOf(
                        "url" to url,
                        "fileName" to fileName,
                        "comment" to comment
                    )
                )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(downloadRequest)
        }
    }

    private fun isValidDirectUrl(url: String): Boolean {
        val regex = Regex(
            "\\.(mp4|mov|avi|mkv|jpg|jpeg|png|gif|webp|mp3|wav|pdf)\$",
            RegexOption.IGNORE_CASE
        )
        return regex.containsMatchIn(url)
    }

    private fun extractFileName(url: String): String {
        return url.substringAfterLast('/').ifEmpty { "downloaded_content" }
    }
}
