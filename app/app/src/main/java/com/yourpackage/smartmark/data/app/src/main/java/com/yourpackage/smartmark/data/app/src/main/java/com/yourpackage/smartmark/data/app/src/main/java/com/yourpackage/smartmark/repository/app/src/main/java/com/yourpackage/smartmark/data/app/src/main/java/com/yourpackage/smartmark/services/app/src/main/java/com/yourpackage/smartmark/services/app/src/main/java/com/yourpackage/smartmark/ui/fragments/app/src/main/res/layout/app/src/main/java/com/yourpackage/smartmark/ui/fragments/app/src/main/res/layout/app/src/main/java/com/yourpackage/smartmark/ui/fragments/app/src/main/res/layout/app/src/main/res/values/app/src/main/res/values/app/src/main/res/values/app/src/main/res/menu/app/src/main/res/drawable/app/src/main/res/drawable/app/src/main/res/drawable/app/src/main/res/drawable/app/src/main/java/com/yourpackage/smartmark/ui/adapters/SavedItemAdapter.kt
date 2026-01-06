package com.yourpackage.smartmark.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.data.SavedItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SavedItemAdapter(
    private val onOpen: (SavedItem) -> Unit,
    private val onShare: (SavedItem) -> Unit,
    private val onDelete: (SavedItem) -> Unit,
    private val onChangeFolder: (SavedItem) -> Unit
) : RecyclerView.Adapter<SavedItemAdapter.ViewHolder>() {

    private var items = listOf<SavedItem>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.title)
        val urlView: TextView = itemView.findViewById(R.id.url)
        val typeView: TextView = itemView.findViewById(R.id.type)
        val qualityView: TextView = itemView.findViewById(R.id.quality)
        val sizeView: TextView = itemView.findViewById(R.id.size)
        val dateView: TextView = itemView.findViewById(R.id.date)
        val shareButton: ImageButton = itemView.findViewById(R.id.share_button)
        val folderButton: ImageButton = itemView.findViewById(R.id.folder_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.titleView.text = item.title
        holder.urlView.text = item.url
        holder.typeView.text = item.type
        holder.qualityView.text = item.quality
        holder.dateView.text = formatTimestamp(item.timestamp)

        val file = File(item.localPath)
        holder.sizeView.text = if (file.exists()) formatFileSize(file.length()) else "N/A"

        holder.itemView.setOnClickListener {
            onOpen(item)
        }

        holder.shareButton.setOnClickListener {
            onShare(item)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(item)
        }

        holder.folderButton.setOnClickListener {
            onChangeFolder(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newList: List<SavedItem>) {
        val diffCallback = SavedItemDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun formatFileSize(sizeInBytes: Long): String {
        val sizeInKB = sizeInBytes / 1024.0
        val sizeInMB = sizeInKB / 1024.0
        return if (sizeInMB > 1) {
            "%.2f MB".format(sizeInMB)
        } else {
            "%.2f KB".format(sizeInKB)
        }
    }

    class SavedItemDiffCallback(
        private val oldList: List<SavedItem>,
        private val newList: List<SavedItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
