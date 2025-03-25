package com.example.wallipop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide




//class ImageAdapter(private val context: Context, private val images: List<ListOfPhotosItem>) :
//    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
//
//    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.img1)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.imagedisp, parent, false)
//        return ImageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
//        Glide.with(context).load(images[position].urls.regular).into(holder.imageView)
//    }
//
//    override fun getItemCount(): Int {
//        return images.size
//    }
//}
class ImageAdapter(private val context: Context, private val photoList: MutableList<ListOfPhotosItem>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.imagedisp, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val photo = photoList[position]

        // Use Glide to load the image from the URL
        Glide.with(context).load(photo.urls.regular).into(holder.img1)

//        holder.img1.setOnClickListener{
//            val intent = Intent(it.context, showimg::class.java)
//            intent.putExtra("key", photoList[position].urls.regular)
//            it.context.startActivity(intent)
//        }
        holder.img1.setOnClickListener {
            val intent = Intent(it.context, showimg::class.java)
            intent.putStringArrayListExtra("imageList", ArrayList(photoList.map { photo -> photo.urls.regular }))
            intent.putExtra("position", position) // Pass clicked image position
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return photoList.size
    }

   class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img1: ImageView = itemView.findViewById(R.id.img1)
    }
}
