package com.example.wallipop

import android.app.AlertDialog
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import carbon.widget.Button
import com.bumptech.glide.Glide
import com.example.wallipop.databinding.ActivityFavoriteshowBinding
import com.squareup.picasso.Picasso
import java.io.IOException

class favoriteshow : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteshowBinding
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     binding= ActivityFavoriteshowBinding.inflate(layoutInflater)
        setContentView(binding.root)


        button=findViewById(R.id.setaswp2)
        imageView=findViewById(R.id.anurag_fav)
        val imageUrl = intent?.getStringExtra("power")
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView)
        }
        else {
            Toast.makeText(this, "Invalid Image URL", Toast.LENGTH_SHORT).show()
        }
        button.setOnClickListener{
            setWallpaper(imageView)
        }
    }
    private fun setWallpaper(imageView: ImageView) {
        val bitmapDrawable = (imageView.drawable as BitmapDrawable).bitmap
        val wallpaperManager = WallpaperManager.getInstance(this)

        AlertDialog.Builder(this)
            .setTitle("Set Wallpaper")
            .setMessage("Where do you want to set the wallpaper?")
            .setPositiveButton("Home Screen") { _, _ ->
                setWallpaperWithFlag(wallpaperManager, bitmapDrawable, 1)
            }
            .setNegativeButton("Lock Screen") { _, _ ->
                setWallpaperWithFlag(wallpaperManager, bitmapDrawable, 2)
            }
            .show()
    }

    private fun setWallpaperWithFlag(wallpaperManager: WallpaperManager, bitmap: Bitmap, flag: Int) {
        try {
            wallpaperManager.setBitmap(bitmap, null, true, flag)
            Toast.makeText(this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
        }
    }
}