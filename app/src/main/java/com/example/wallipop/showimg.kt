//package com.example.wallipop
//
//import android.os.Bundle
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatButton
//import androidx.cardview.widget.CardView
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.squareup.picasso.Picasso
//
//class showimg : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_showimg)
//
//        val id = findViewById<ImageView>(R.id.showimg)
//        val setBtn = findViewById<CardView>(R.id.setaswp)
//        val image = intent?.getStringExtra("key")
//
//        if (!image.isNullOrEmpty()) {
//            Picasso.get().load(image).into(id)
//        } else {
//            Toast.makeText(this, "Invalid Image URL", Toast.LENGTH_SHORT).show()
//            return
//        }
//    }
//
//}





package com.example.wallipop

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import carbon.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.IOException

class showimg : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showimg)

        sharedPreferences=getSharedPreferences("aalu",MODE_PRIVATE)

        val image = findViewById<ImageView>(R.id.showimg)
        val setBtn = findViewById<TextView>(R.id.setaswp)
        val fav = findViewById<ImageView>(R.id.favourite)
        val dld = findViewById<ImageView>(R.id.downld)
        val imageUrl = intent?.getStringExtra("key")
progress=findViewById(R.id.progressbar2)



        database = FirebaseDatabase.getInstance().getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()



        fav.setOnClickListener {
progress.visibility= View.VISIBLE
            val currentUser = firebaseAuth.currentUser
            if (imageUrl != null && currentUser != null) {
                val userFavoritesRef = database.child(currentUser.uid).child("favorites")
                val imageKey = imageUrl.replace(".", "_").replace("/", "_") // Firebase doesn't allow certain characters in keys

                userFavoritesRef.child(imageKey).get().addOnSuccessListener {
                    if (it.exists()) {
                        Toast.makeText(this, "Already in Favorites!", Toast.LENGTH_SHORT).show()
                        progress.visibility= View.GONE
                    } else {
                        val user = UserDefined(imageUrl)
                        userFavoritesRef.child(imageKey).setValue(user)
                            .addOnSuccessListener {
                                fav.setColorFilter(ContextCompat.getColor(this, R.color.background_color))
                                Toast.makeText(this, "Added to Favorites!", Toast.LENGTH_SHORT).show()
                                progress.visibility= View.GONE

                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Failed to save: ${exception.message}", Toast.LENGTH_LONG).show()
                                progress.visibility= View.GONE
                                Log.e("FirebaseError", "Error saving data", exception)
                            }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error checking favorites: ${exception.message}", Toast.LENGTH_LONG).show()
                    progress.visibility= View.GONE
                    Log.e("FirebaseError", "Error checking data", exception)
                }
            } else {
                Toast.makeText(this, "Image URL or User is null", Toast.LENGTH_SHORT).show()
                progress.visibility= View.GONE
            }
        }








        if (imageUrl!=null) {
            Picasso.get().load(imageUrl).into(image)


        } else {
            Toast.makeText(this, "Invalid Image URL", Toast.LENGTH_SHORT).show()
        }

        setBtn.setOnClickListener {
            setWallpaper(image)
        }
    }

    private fun setWallpaper(imageView: ImageView) {
        val bitmapDrawable = (imageView.drawable as BitmapDrawable).bitmap // The imageView is an instance of ImageView that holds an image (which is typically a drawable).
        //        The drawable property of an ImageView refers to the Drawable object that is being displayed in the ImageView.
        //        A Drawable is a general representation of an image or a graphic in Android, which can be a bitmap, shape, vector, etc.
        //        The drawable property can be any type of Drawable, but in this case, you're expecting it to be a BitmapDrawable, which is a specific subclass of Drawable that holds a bitmap (i.e., an image).
        //        The as keyword is a type cast operation. It tells the compiler that you're expecting imageView.drawable to be a BitmapDrawable. If it's not a BitmapDrawable, it will throw a ClassCastException.
        //        This casting is necessary because the drawable property is a general Drawable, and you need to convert it to BitmapDrawable to access the specific properties and methods of a BitmapDrawable.




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






