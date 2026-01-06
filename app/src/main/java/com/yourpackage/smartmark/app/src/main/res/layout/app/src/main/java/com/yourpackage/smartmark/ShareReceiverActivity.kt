package com.yourpackage.smartmark

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yourpackage.smartmark.services.ContentDownloader

class ShareReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    val url = extractUrl(sharedText)
                    if (url != null && isValidDirectUrl(url)) {
                        showCustomizeDialog(url)
                    } else {
                        Toast.makeText(this, "No valid direct URL found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "No text shared", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun showCustomizeDialog(url: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_customize, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.title_input)
        val commentInput = dialogView.findViewById<EditText>(R.id.comment_input)

        AlertDialog.Builder(this)
            .setTitle("Customize Download")
            .setView(dialogView)
            .setPositiveButton("Download") { _, _ ->
                val title = titleInput.text.toString().ifEmpty { extractFileName(url) }
                val comment = commentInput.text.toString()
                ContentDownloader.downloadContentWithDetails(this, url, title, comment)
                Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()
    }

    private fun extractUrl(text: String): String? {
        val regex = Regex("https?://[\\w.-]+(?:\\.[\\w\\.-]+)+[/\\w\\.-]*")
        return regex.find(text)?.value
    }

    private fun isValidDirectUrl(url: String): Boolean {
        val regex = Regex("\\.(mp4|mov|avi|mkv|jpg|jpeg|png|gif|webp|mp3|wav|pdf)\$", RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(url)
    }

    private fun extractFileName(url: String): String {
        return url.substringAfterLast('/').ifEmpty { "downloaded_content" }
    }
}
