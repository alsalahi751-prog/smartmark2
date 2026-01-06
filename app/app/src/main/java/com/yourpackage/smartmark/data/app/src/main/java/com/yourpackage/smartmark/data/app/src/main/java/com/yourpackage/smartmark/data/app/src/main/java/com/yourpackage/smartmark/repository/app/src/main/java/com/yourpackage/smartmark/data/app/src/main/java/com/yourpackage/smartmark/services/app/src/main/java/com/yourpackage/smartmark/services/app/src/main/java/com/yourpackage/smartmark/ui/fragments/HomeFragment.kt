package com.yourpackage.smartmark.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.databinding.FragmentHomeBinding
import com.yourpackage.smartmark.services.ContentDownloader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val urlInput = binding.urlInput
        val downloadBtn = binding.downloadBtn

        downloadBtn.setOnClickListener {
            val input = urlInput.text.toString().trim()
            if (input.isNotEmpty()) {
                val urls = input.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                if (urls.isNotEmpty()) {
                    urls.forEach { url ->
                        if (isValidDirectUrl(url)) {
                            showCustomizeDialog(url)
                        }
                    }
                } else {
                    Toast.makeText(context, "No valid URLs found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter URLs", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showCustomizeDialog(url: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_customize, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.title_input)
        val commentInput = dialogView.findViewById<EditText>(R.id.comment_input)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Customize Download")
            .setView(dialogView)
            .setPositiveButton("Download") { _, _ ->
                val title = titleInput.text.toString().ifEmpty { extractFileName(url) }
                val comment = commentInput.text.toString()
                ContentDownloader.downloadContentWithDetails(requireContext(), url, title, comment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun isValidDirectUrl(url: String): Boolean {
        val regex = Regex("\\.(mp4|mov|avi|mkv|jpg|jpeg|png|gif|webp|mp3|wav|pdf)\$", RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(url)
    }

    private fun extractFileName(url: String): String {
        return url.substringAfterLast('/').ifEmpty { "downloaded_content" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
