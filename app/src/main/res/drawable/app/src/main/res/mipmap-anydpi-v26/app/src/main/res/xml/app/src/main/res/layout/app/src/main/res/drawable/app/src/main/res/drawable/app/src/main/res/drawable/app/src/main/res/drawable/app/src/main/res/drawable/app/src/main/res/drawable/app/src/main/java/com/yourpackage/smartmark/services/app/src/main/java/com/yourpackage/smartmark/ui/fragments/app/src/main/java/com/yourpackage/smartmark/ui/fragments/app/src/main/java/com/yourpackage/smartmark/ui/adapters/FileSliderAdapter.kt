package com.yourpackage.smartmark.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yourpackage.smartmark.R
import com.yourpackage.smartmark.data.SavedItem

class FileSliderAdapter(
    private val items: List<SavedItem>
) : RecyclerView.Adapter<FileSliderAdapter.SliderViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position].type == "video") 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val layout = if (viewType == 1) R.layout.item_slider_video else R.layout.item_slider_image
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = items[position]

        if (holder.imageView != null) {
            Glide.with(holder.imageView.context)
                .load(item.localPath)
                .into(holder.imageView)
        }

        if (holder.videoView != null) {
            holder.videoView.setVideoPath(item.localPath)
            holder.videoView.start()
        }
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView? = itemView.findViewById(R.id.slider_image)
        val videoView: VideoView? = itemView.findViewById(R.id.slider_video)
    }
}
