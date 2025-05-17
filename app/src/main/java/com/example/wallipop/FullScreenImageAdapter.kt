package com.example.wallipop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import carbon.widget.ImageView
import com.bumptech.glide.Glide

class FullScreenImageAdapter(
    private val imageList: List<String>,
    private val context: Context
) : RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.fullscreenImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fullscreen_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageList[position]
        Glide.with(context).load(imageUrl).into(holder.imageView)
    }


    fun getImageAt(position: Int): String {
        return imageList[position]
    }
    override fun getItemCount(): Int = imageList.size
}
