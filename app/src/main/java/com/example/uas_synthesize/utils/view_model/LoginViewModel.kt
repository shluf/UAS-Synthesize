package com.example.uas_synthesize.utils.view_model

import androidx.lifecycle.ViewModel
import com.example.uas_synthesize.data.model.get.User
import com.example.uas_synthesize.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    private val apiService = ApiClient.getInstance()

    suspend fun loginUser(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val users = apiService.getUsers()
                users.find { it.username == username && it.password == password }
            } catch (e: Exception) {
                null
            }
        }
    }
}