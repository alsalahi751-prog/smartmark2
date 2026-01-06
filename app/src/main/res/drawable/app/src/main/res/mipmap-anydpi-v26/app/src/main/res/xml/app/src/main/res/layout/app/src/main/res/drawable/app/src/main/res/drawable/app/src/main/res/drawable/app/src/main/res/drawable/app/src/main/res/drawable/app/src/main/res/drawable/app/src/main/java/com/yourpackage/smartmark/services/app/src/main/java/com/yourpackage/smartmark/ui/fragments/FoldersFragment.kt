package com.yourpackage.smartmark.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.data.FolderCount
import com.yourpackage.smartmark.databinding.FragmentFoldersBinding
import com.yourpackage.smartmark.repository.SavedItemRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FoldersFragment : Fragment() {

    @Inject
    lateinit var repository: SavedItemRepository

    private var _binding: FragmentFoldersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView = binding.foldersRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val foldersWithCount = repository.getFolderCounts()

            val adapter = FoldersAdapter(foldersWithCount) { folder ->
                // Handle folder click
            }

            recyclerView.adapter = adapter
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
