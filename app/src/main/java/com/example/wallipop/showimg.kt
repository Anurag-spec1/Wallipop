package com.example.wallipop

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import carbon.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class showimg : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progress: ProgressBar
    private lateinit var viewPager: ViewPager2
    private lateinit var fav: ImageView
    private lateinit var dld: ImageView
    private lateinit var setBtn: TextView

    private lateinit var imageList: List<String>
    private var currentPosition: Int = 0  // To track current image position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showimg)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        progress = findViewById(R.id.progressbar2)
        viewPager = findViewById(R.id.viewPager)
        fav = findViewById(R.id.favourite)
        dld = findViewById(R.id.downld)
        setBtn = findViewById(R.id.setaswp)

        // Receive the image list and selected position
        imageList = intent.getStringArrayListExtra("imageList") ?: emptyList()
        currentPosition = intent.getIntExtra("position", 0)

        val adapter = FullScreenImageAdapter(imageList, this)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentPosition, false)

        // Swipe listener to update position
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }
        })

        // Favorite Button
        fav.setOnClickListener {
            addToFavorites(imageList[currentPosition])
        }

        // Download Button
        dld.setOnClickListener {
            downloadImage(imageList[currentPosition])
        }

        // Set Wallpaper Button
        setBtn.setOnClickListener {
            setWallpaper()
        }
    }

    private fun addToFavorites(imageUrl: String) {
        progress.visibility = View.VISIBLE
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userFavoritesRef = database.child(currentUser.uid).child("favorites")
            val imageKey = imageUrl.replace(".", "_").replace("/", "_")

            userFavoritesRef.child(imageKey).get().addOnSuccessListener {
                if (it.exists()) {
                    Toast.makeText(this, "Already in Favorites!", Toast.LENGTH_SHORT).show()
                } else {
                    val user = UserDefined(imageUrl)
                    userFavoritesRef.child(imageKey).setValue(user)
                        .addOnSuccessListener {
                            fav.setColorFilter(ContextCompat.getColor(this, R.color.background_color))
                            Toast.makeText(this, "Added to Favorites!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to save: ${exception.message}", Toast.LENGTH_LONG).show()
                            Log.e("FirebaseError", "Error saving data", exception)
                        }
                }
                progress.visibility = View.GONE
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking favorites: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("FirebaseError", "Error checking data", exception)
                progress.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            progress.visibility = View.GONE
        }
    }

    private fun setWallpaper() {
        val adapter = viewPager.adapter as FullScreenImageAdapter
        val imageUrl = adapter.getImageAt(currentPosition)

        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    setWallpaperWithBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun setWallpaperWithBitmap(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(this)

        AlertDialog.Builder(this)
            .setTitle("Set Wallpaper")
            .setMessage("Where do you want to set the wallpaper?")
            .setPositiveButton("Home Screen") { _, _ ->
                setWallpaperWithFlag(wallpaperManager, bitmap, WallpaperManager.FLAG_SYSTEM)
            }
            .setNegativeButton("Lock Screen") { _, _ ->
                setWallpaperWithFlag(wallpaperManager, bitmap, WallpaperManager.FLAG_LOCK)
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

    private fun downloadImage(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    saveImageToGallery(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val fos: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)))
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Image Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }
}
