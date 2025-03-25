package com.example.wallipop


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carbon.Carbon
import carbon.widget.Button
import carbon.widget.ImageView
import carbon.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var signOutButton: ImageView

    private val imageList = mutableListOf<ListOfPhotosItem>()
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)  //requirecontext() means agar fragment se attch koi activity na ho to exception through kr dega

        signOutButton = view.findViewById(R.id.sign_out)
        shimmer = view.findViewById(R.id.ShimmerFrameLayout)
        recyclerView = view.findViewById(R.id.recycle)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        imageAdapter = ImageAdapter(requireContext(), imageList)
        recyclerView.adapter = imageAdapter

        signOutButton.setOnClickListener {
            signOutUser()
        }


        // Fetch initial images
        getImages()

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Trigger loading when 5 items away from the end
                if (!isLoading && !isLastPage && totalItemCount <= (lastVisibleItem + 5)) {
                    loadMoreImages()
                }
            }
        })

        return view
    }

    private fun getImages() {
        if (isLoading || isLastPage) return

        shimmer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        isLoading = true

        val call = ImageService.imageInstance.getPhotos(currentPage, "8sYNuHtHI6Cqb9cUmRdhGaCnc6JLfy8gC9o5gROY-P0")
        call.enqueue(object : Callback<List<ListOfPhotosItem>> {
            override fun onResponse(
                call: Call<List<ListOfPhotosItem>>,
                response: Response<List<ListOfPhotosItem>>
            ) {
                isLoading = false
                shimmer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    val photos = response.body()
                    if (!photos.isNullOrEmpty()) {
                        imageList.addAll(photos)
                        imageAdapter.notifyItemRangeInserted(imageList.size - photos.size, photos.size)

                        // If fewer items are returned than expected, mark as last page
                        if (photos.size < 10) {
                            isLastPage = true
                        }
                    } else {
                        isLastPage = true
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load images: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ListOfPhotosItem>>, t: Throwable) {
                isLoading = false
                shimmer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                Log.e("HomeFragment", "Error fetching images", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadMoreImages() {
        if (isLastPage || isLoading) return

        currentPage++ // Increment the page
        isLoading = true

        val call = ImageService.imageInstance.getPhotos(currentPage, "8sYNuHtHI6Cqb9cUmRdhGaCnc6JLfy8gC9o5gROY-P0")
        call.enqueue(object : Callback<List<ListOfPhotosItem>> {
            override fun onResponse(
                call: Call<List<ListOfPhotosItem>>,
                response: Response<List<ListOfPhotosItem>>
            ) {
                isLoading = false

                if (response.isSuccessful) {
                    val photos = response.body()
                    if (!photos.isNullOrEmpty()) {
                        imageList.addAll(photos)
                        imageAdapter.notifyItemRangeInserted(imageList.size - photos.size, photos.size)

                        // Mark as last page if fewer items are returned
                        if (photos.size < 10) {
                            isLastPage = true
                        }
                    } else {
                        isLastPage = true
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load more images: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ListOfPhotosItem>>, t: Throwable) {
                isLoading = false
                Log.e("HomeFragment", "Error loading more images", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signOutUser() {
        firebaseAuth.signOut()
        sharedPreferences.edit().putBoolean("learn", false).apply()

        val intent = Intent(requireContext(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
            requireActivity().finishAffinity()
    }
}









