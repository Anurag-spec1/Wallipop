package com.example.wallipop

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.Button
import com.bumptech.glide.Glide

class MediaAdapter(private val context: Context,private val mediaList: MutableList<Uri>) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val Button: Button= itemView.findViewById(R.id.Button)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.imagepicker, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val imageUri = mediaList[position]

        // Use Glide to load the image from the URI
        Glide.with(holder.imageView.context)
            .load(imageUri)  // Load the image URI
            .into(holder.imageView) // Set the image into the ImageView



        holder.Button.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Set Wallpaper")
                .setMessage("Set wallpaper for Home Screen or Lock Screen?")
                .setPositiveButton("Home Screen") { _, _ ->
                    setAsWallpaper(imageUri, WallpaperManager.FLAG_SYSTEM) // Home Screen
                }
                .setNegativeButton("Lock Screen") { _, _ ->
                    setAsWallpaper(imageUri, WallpaperManager.FLAG_LOCK) // Lock Screen
                }
                .setNeutralButton("Cancel", null)
                .show()
        }




    }

    override fun getItemCount(): Int = mediaList.size


    private fun setAsWallpaper(imageUri: Uri, flag: Int) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val wallpaperManager = WallpaperManager.getInstance(context)

            // Use the flag to set the wallpaper
            wallpaperManager.setBitmap(bitmap, null, true, flag)

            Toast.makeText(context, "Wallpaper set successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
        }
    }

}

