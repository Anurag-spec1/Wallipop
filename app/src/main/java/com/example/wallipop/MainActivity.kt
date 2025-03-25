package com.example.wallipop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.wallipop.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class MainActivity : AppCompatActivity() {
//
//    private lateinit var viewPager2: ViewPager2
//    private lateinit var bottomNavigationView: BottomNavigationView
//    private lateinit var adapter: myViewAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize the ViewPager2 and BottomNavigationView
//        viewPager2 = findViewById(R.id.viewPager)
//        bottomNavigationView = findViewById(R.id.bottomNavigationView)
//
//        // Set up the adapter with fragments
//        adapter = myViewAdapter(supportFragmentManager,lifecycle)
//        viewPager2.adapter = adapter
//
//
//
//
//
//
//        // BottomNavigationView listener to change ViewPager2 page
//        bottomNavigationView.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.Home -> viewPager2.currentItem = 0
//                R.id.Collection -> viewPager2.currentItem = 1
//            }
//            true
//        }
//
//        // Automatically sync BottomNavigationView when ViewPager2 page changes
//        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                bottomNavigationView.selectedItemId = when (position) {
//                    0 -> R.id.Home
//                    1 -> R.id.Collection
//                    else -> throw IllegalArgumentException("Unknown position")
//                }
//            }
//        })
//    }
//
//    override fun onBackPressed() {
//        finishAffinity()
//    }
//}
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

firebaseAuth= FirebaseAuth.getInstance()

        binding.bottomNavigationView.add(
            CurvedBottomNavigation.Model(1,"Home", R.drawable.baseline_add_home_24)
        )
        binding.bottomNavigationView.add(
            CurvedBottomNavigation.Model(2,"Collections", R.drawable.baseline_account_circle_24)
        )
        binding.bottomNavigationView.add(
            CurvedBottomNavigation.Model(3,"Favorites",R.drawable.baseline_favorite_24)
        )

        binding.bottomNavigationView.setOnClickMenuListener {
            when(it.id){
                1 -> changeFragment(HomeFragment())
                2 -> changeFragment(PersonalFragment())
                3-> changeFragment(Favorite())
            }
            true
        }
        changeFragment(HomeFragment())
        binding.bottomNavigationView.show(1)
    }
    private fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.Frame,fragment)
            commit()
        }
    }
    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        if (user == null) {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    override fun onBackPressed() {
        finishAffinity()
    }
}

