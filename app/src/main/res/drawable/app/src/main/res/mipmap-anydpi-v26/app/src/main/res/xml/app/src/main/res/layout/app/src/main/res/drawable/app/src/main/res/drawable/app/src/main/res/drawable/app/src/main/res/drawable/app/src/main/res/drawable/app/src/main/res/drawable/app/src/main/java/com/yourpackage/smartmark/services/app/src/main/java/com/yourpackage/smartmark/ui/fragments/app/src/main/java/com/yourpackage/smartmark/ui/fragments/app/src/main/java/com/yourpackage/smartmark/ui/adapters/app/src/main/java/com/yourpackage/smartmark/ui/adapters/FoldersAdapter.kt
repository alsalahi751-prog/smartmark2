package com.yourpackage.smartmark.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.data.FolderCount

class FoldersAdapter(
    private val folders: List<FolderCount>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<FoldersAdapter.FolderViewHolder>() {

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName: TextView = itemView.findViewById(R.id.folder_name)
        val fileCount: TextView = itemView.findViewById(R.id.file_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.folderName.text = folder.folder
        holder.fileCount.text = folder.count.toString()
        holder.itemView.setOnClickListener {
            onItemClick(folder.folder)
        }
    }

    override fun getItemCount(): Int = folders.size
}
