package com.example.uas_synthesize.data.model.view

import androidx.lifecycle.ViewModel
import com.example.uas_synthesize.data.model.get.Profile
import com.example.uas_synthesize.data.model.get.User
import com.example.uas_synthesize.data.model.post.UserSend
import com.example.uas_synthesize.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {
    private val apiService = ApiClient.getInstance()

    suspend fun registerUser(name: String, username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                val newUser = Profile(name = name, avatar = "", bio = "Seorang jiwa penasaran yang menikmati petualangan. - $name")
                apiService.registerUser(
                    UserSend(
                        username = username,
                        password = password,
                        profile = newUser
                    )
                )
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}