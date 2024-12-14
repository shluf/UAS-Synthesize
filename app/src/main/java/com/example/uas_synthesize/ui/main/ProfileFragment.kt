package com.example.uas_synthesize.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.database.ChatDatabase
import com.example.uas_synthesize.data.database.LikedThreadDatabase
import com.example.uas_synthesize.data.model.get.Profile
import com.example.uas_synthesize.data.model.post.UserSend
import com.example.uas_synthesize.data.network.ApiClient
import com.example.uas_synthesize.data.network.CloudinaryManager
import com.example.uas_synthesize.databinding.FragmentProfileBinding
import com.example.uas_synthesize.ui.auth.LoginActivity
import com.example.uas_synthesize.utils.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var prefManager: PrefManager
    private lateinit var chatDatabase: ChatDatabase
    private lateinit var likedDatabase: LikedThreadDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatDatabase = ChatDatabase.getInstance(requireContext())
        likedDatabase = LikedThreadDatabase.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())

        if (prefManager.isGuest()) {
            displayGuestUserData()
        } else {
            displayUserData()
            binding.ivAvatar.setOnClickListener {
                checkAndRequestMediaPermission()
            }
        }

        with(binding) {
            btnLogout.setOnClickListener {
                logout()
            }
        }
    }

    private fun checkAndRequestMediaPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission Required")
                    .setMessage("We need access to your media to change your profile picture.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                showToast("Permission denied")
            }
        }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                uploadImageToCloudinary(imageUri)
            } else {
                showToast("Image selection failed")
            }
        }
    }

    private fun uploadImageToCloudinary(imageUri: Uri) {
        lifecycleScope.launch {
            try {
                val file = convertUriToFile(imageUri)
                val response = withContext(Dispatchers.IO) {
                    CloudinaryManager.uploadImage(file)
                }

                if (response.isSuccessful) {
                    val imageUrl = response.url
                    if (!imageUrl.isNullOrEmpty()) {
                        saveImageUrlToApi(imageUrl)
                    } else {
                        showToast("Failed to retrieve image URL")
                    }
                } else {
                    showToast("Image upload failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("An error occurred: ${e.message}")
            }
        }
    }

    private fun convertUriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val tempFile = File(requireContext().cacheDir, "temp_image.jpg")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun saveImageUrlToApi(imageUrl: String) {
        lifecycleScope.launch {
            try {
                // Show loading
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                // 1. Get current user data
                val currentUser = ApiClient.getInstance().getUser(prefManager.getUserId())

                // 2. Prepare updated profile with new avatar
                val updatedProfile = Profile(
                    name = currentUser.profile.name,
                    avatar = imageUrl,
                    bio = currentUser.profile.bio
                )

                // 3. Create UserSend object with existing data and new avatar
                val updateRequest = UserSend(
                    username = currentUser.username,
                    password = currentUser.password,
                    profile = updatedProfile
                )

                // 4. Update user profile
                ApiClient.getInstance().updateUser(
                    userId = currentUser._id,
                    user = updateRequest
                )

                // 5. Update local preferences
                prefManager.saveAvatar(imageUrl)

                // 6. Update UI
                withContext(Dispatchers.Main) {
                    Glide.with(this@ProfileFragment)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_error_avatar)
                        .into(binding.ivAvatar)

                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Profile picture updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Update failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }


    private fun showToast(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayGuestUserData() {
        val username = ""
        val name = "Guest"
        val avatarUrl = ""
        val bio = "You're logged in as guest"

        binding.tvUsername.text = username
        binding.tvName.text = name
        binding.tvBio.text = bio

        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_error_avatar)
            .into(binding.ivAvatar)
    }

    private fun displayUserData() {
        val username = prefManager.getUsername()
        val name = prefManager.getName()
        val avatarUrl = prefManager.getAvatar()
        val bio = prefManager.getBio()

        binding.tvUsername.text = "@$username"
        binding.tvName.text = name
        binding.tvBio.text = bio

        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_error_avatar)
            .into(binding.ivAvatar)
    }

    private fun logout() {
        lifecycleScope.launch {
            prefManager.clear()
            prefManager.setLoggedIn(false)

            chatDatabase.chatDao().deleteAllChats()
            chatDatabase.chatDao().deleteAllMessages()
            likedDatabase.likedThreadDao().deleteAllLikedThread()
            likedDatabase.likedThreadDao().deleteAllComment()

            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    companion object {
        private const val IMAGE_PICK_REQUEST_CODE = 1001

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}