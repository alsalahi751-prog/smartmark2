package com.yourpackage.smartmark.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.databinding.FragmentSettingsBinding
import com.yourpackage.smartmark.services.BackupManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        setupThemeToggle()
        setupBackupRestore()
        setupUpdateCheck()
        setupContactSupport()
        setupRateApp()
        setupLanguageToggle()

        return view
    }

    private fun setupThemeToggle() {
        binding.themeToggleButton.setOnClickListener {
            toggleTheme()
        }
        updateThemeButtonText()
    }

    private fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        AppCompatDelegate.setDefaultNightMode(newMode)
        updateThemeButtonText()
    }

    private fun updateThemeButtonText() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val text = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            "Switch to Light Mode"
        } else {
            "Switch to Dark Mode"
        }
        binding.themeToggleButton.text = text
    }

    private fun setupBackupRestore() {
        binding.backupRestoreButton.setOnClickListener {
            showBackupRestoreDialog()
        }
    }

    private fun showBackupRestoreDialog() {
        val options = arrayOf("Create Backup", "Restore from Backup")

        AlertDialog.Builder(requireContext())
            .setTitle("Backup & Restore")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startBackup()
                    1 -> startRestore()
                }
            }
            .show()
    }

    private fun startBackup() {
        lifecycleScope.launch {
            val success = BackupManager.createBackup(requireContext())
            if (success) {
                Toast.makeText(context, "Backup created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Backup failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRestore() {
        lifecycleScope.launch {
            val success = BackupManager.restoreBackup(requireContext())
            if (success) {
                Toast.makeText(context, "Restore completed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Restore failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUpdateCheck() {
        binding.checkUpdateButton.setOnClickListener {
            // يمكنك إضافة دعم التحديث هنا
            Toast.makeText(context, "Check for updates feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupContactSupport() {
        binding.contactSupportButton.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_support, null)
            val messageInput = dialogView.findViewById<EditText>(R.id.message_input)

            AlertDialog.Builder(requireContext())
                .setTitle("Contact Support")
                .setView(dialogView)
                .setPositiveButton("Send") { _, _ ->
                    val message = messageInput.text.toString().trim()
                    if (message.isNotEmpty()) {
                        sendSupportEmail(message)
                    } else {
                        Toast.makeText(context, "Please enter your message", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun sendSupportEmail(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@smartmark.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Support Request from SmartMark App")
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send email via..."))
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRateApp() {
        binding.rateAppButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${requireContext().packageName}")
                }
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                }
                startActivity(intent)
            }
        }
    }

    private fun setupLanguageToggle() {
        binding.btnToggleAr.setOnClickListener {
            // يمكنك إضافة تبديل اللغة هنا
            Toast.makeText(context, "Arabic language selected", Toast.LENGTH_SHORT).show()
        }

        binding.btnToggleEn.setOnClickListener {
            // يمكنك إضافة تبديل اللغة هنا
            Toast.makeText(context, "English language selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
