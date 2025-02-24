package com.example.wallipop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallipop.databinding.FragmentPersonalBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class PersonalFragment : Fragment() {
    private lateinit var binding: FragmentPersonalBinding
    private lateinit var mediaUriList: MutableList<Uri>
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalBinding.inflate(inflater, container, false)

        // Get the current user's UID
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        // Initialize the list for images and adapter
        mediaUriList = mutableListOf()
        mediaAdapter = MediaAdapter(requireContext(),mediaUriList)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mediaAdapter

        // Image picker registration
        val galleryImagePicker =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val savedFile = saveImageToInternalStorage(it)
                    val imageUri = Uri.fromFile(savedFile)
                    mediaUriList.add(imageUri)
                    mediaAdapter.notifyDataSetChanged()
                }
            }

        // Set button click listener
        binding.pickImageButton.setOnClickListener {
            galleryImagePicker.launch("image/*")
        }

        // Load previously saved images for the current user
        loadUserImages()


        return binding.root
    }


    private fun getUserImagesDirectory(): File {
        val userDirectory = File(requireContext().filesDir, "user_images/$currentUserId")
        if (!userDirectory.exists()) {
            userDirectory.mkdirs()
        }
        return userDirectory
    }

    private fun saveImageToInternalStorage(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val userDirectory = getUserImagesDirectory()

        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(userDirectory, fileName)

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun loadUserImages() {
        val images = getAllImagesForUser()
        mediaUriList.clear()
        mediaUriList.addAll(images.map { Uri.fromFile(it) })
        mediaAdapter.notifyDataSetChanged()
    }

    private fun getAllImagesForUser(): List<File> {
        val userDirectory = getUserImagesDirectory()
        return userDirectory.listFiles { _, name -> name.endsWith(".jpg") }?.toList() ?: emptyList()
    }
}








