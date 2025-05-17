package com.example.wallipop


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
    private lateinit var searchView: SearchView
    private lateinit var imageAdapter: ImageAdapter

    private val imageList = mutableListOf<ListOfPhotosItem>()
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private var currentQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        shimmer = view.findViewById(R.id.ShimmerFrameLayout)
        recyclerView = view.findViewById(R.id.recycle)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        imageAdapter = ImageAdapter(requireContext(), imageList)
        recyclerView.adapter = imageAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query
                currentPage = 1
                isLastPage = false
                imageList.clear()
                imageAdapter.notifyDataSetChanged()
                if (query != null && query.isNotEmpty()) {
                    searchImages(query)
                } else {
                    getImages()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        getImages()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!isLoading && !isLastPage && totalItemCount <= lastVisibleItem + 5) {
                    if (currentQuery != null) {
                        searchImages(currentQuery!!)
                    } else {
                        getImages()
                    }
                }
            }
        })

        return view
    }

    private fun getImages() {
        isLoading = true
        shimmer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        ImageService.imageInstance.getPhotos(currentPage).enqueue(object : Callback<List<ListOfPhotosItem>> {
            override fun onResponse(call: Call<List<ListOfPhotosItem>>, response: Response<List<ListOfPhotosItem>>) {
                shimmer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                isLoading = false

                response.body()?.let {
                    imageList.addAll(it)
                    imageAdapter.notifyDataSetChanged()
                    if (it.size < 10) isLastPage = true
                    currentPage++
                }
            }

            override fun onFailure(call: Call<List<ListOfPhotosItem>>, t: Throwable) {
                shimmer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                isLoading = false
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchImages(query: String) {
        isLoading = true
        shimmer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        ImageService.imageInstance.searchPhotos(query, currentPage).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                shimmer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                isLoading = false

                response.body()?.results?.let {
                    imageList.addAll(it)
                    imageAdapter.notifyDataSetChanged()
                    if (it.size < 10) isLastPage = true
                    currentPage++
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                shimmer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                isLoading = false
                Toast.makeText(requireContext(), "Search error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}










