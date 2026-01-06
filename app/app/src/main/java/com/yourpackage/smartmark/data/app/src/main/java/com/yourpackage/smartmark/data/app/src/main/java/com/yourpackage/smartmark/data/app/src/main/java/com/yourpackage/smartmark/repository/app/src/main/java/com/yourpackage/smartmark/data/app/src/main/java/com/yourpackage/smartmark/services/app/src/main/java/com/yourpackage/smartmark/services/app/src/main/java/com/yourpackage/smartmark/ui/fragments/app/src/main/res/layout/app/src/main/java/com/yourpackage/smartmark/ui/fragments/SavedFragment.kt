package com.yourpackage.smartmark.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.databinding.FragmentSavedBinding
import com.yourpackage.smartmark.data.SavedItem
import com.yourpackage.smartmark.repository.SavedItemRepository
import com.yourpackage.smartmark.ui.adapters.SavedItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SavedFragment : Fragment() {

    @Inject
    lateinit var repository: SavedItemRepository

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SavedItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        setupSearchView()
        setupSwipeRefresh()

        return view
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SavedItemAdapter(
            onOpen = { item -> openFile(item.localPath) },
            onShare = { item -> shareFile(item.localPath) },
            onDelete = { item ->
                deleteFile(item)
                Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
            },
            onChangeFolder = { item -> changeFolder(item) }
        )
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            repository.getAllItems().collect { items ->
                adapter.submitList(items)
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText ?: "")
                return true
            }
        })
    }

    private fun filterList(query: String) {
        lifecycleScope.launch {
            repository.searchItems(query).collect { items ->
                adapter.submitList(items)
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshList()
        }
    }

    private fun refreshList() {
        lifecycleScope.launch {
            repository.getAllItems().collect { items ->
                adapter.submitList(items)
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun openFile(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, getMimeType(filePath))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = getMimeType(filePath)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(Intent.createChooser(intent, "Share file via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot share this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFile(item: SavedItem) {
        val file = File(item.localPath)
        if (file.exists()) {
            file.delete()
        }

        lifecycleScope.launch {
            repository.delete(item)
        }
    }

    private fun changeFolder(item: SavedItem) {
        val folders = arrayOf("Instagram", "TikTok", "Facebook", "Twitter", "General")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose folder")
        builder.setItems(folders) { _, which ->
            val newFolder = folders[which]
            updateItemFolder(item, newFolder)
        }
        builder.show()
    }

    private fun updateItemFolder(item: SavedItem, newFolder: String) {
        lifecycleScope.launch {
            val updatedItem = item.copy(folder = newFolder)
            repository.insert(updatedItem)
        }
    }

    private fun getMimeType(filePath: String): String {
        return when {
            filePath.endsWith(".mp4", true) -> "video/mp4"
            filePath.endsWith(".jpg", true) || filePath.endsWith(".jpeg", true) -> "image/jpeg"
            filePath.endsWith(".png", true) -> "image/png"
            else -> "*/*"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
