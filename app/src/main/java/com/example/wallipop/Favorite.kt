package com.example.wallipop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Favorite : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var progressBar: ProgressBar
    private val favoriteList = mutableListOf<UserDefined>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_favorite,container,false)
        recyclerView = view.findViewById(R.id.recycle_favourite)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        favoriteAdapter = FavoriteAdapter(requireContext(), favoriteList)
        recyclerView.adapter = favoriteAdapter
        firebaseAuth = FirebaseAuth.getInstance()
        progressBar=view.findViewById(R.id.progressBar)
        database = FirebaseDatabase.getInstance().getReference("users")

        progressBar.visibility = View.VISIBLE

        loadFavorites()
        return view
    }
    private fun loadFavorites() {

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userFavoritesRef = database.child(currentUser.uid).child("favorites")
            userFavoritesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    favoriteList.clear() // Clear previous data before updating


                        for (favSnapshot in snapshot.children) {
                            val user = favSnapshot.getValue(UserDefined::class.java)
                            if (user != null) {
                                favoriteList.add(user)
                            }
                        }

                    favoriteAdapter.notifyDataSetChanged()
                   progressBar.visibility= View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}