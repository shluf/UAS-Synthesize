package com.example.uas_synthesize.ui.main.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_synthesize.data.model.get.User
import com.example.uas_synthesize.data.model.post.ChatSend
import com.example.uas_synthesize.data.network.ApiClient
import com.example.uas_synthesize.databinding.ActivityAddChatBinding
import com.example.uas_synthesize.utils.PrefManager
import kotlinx.coroutines.launch

class AddChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddChatBinding

    private val apiService = ApiClient.getInstance()
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        fetchUsers()
    }

    private fun fetchUsers() {
        lifecycleScope.launch {
            try {
                val currentUsername = prefManager.getUsername()
                val users = apiService.getUsers().filter { it.username != currentUsername }

                binding.recyclerViewUsers.apply {
                    layoutManager = LinearLayoutManager(this@AddChatActivity)
                    adapter = UserAdapter(users) { user ->
                        createChatWithUser(user)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddChatActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createChatWithUser(user: User) {
        lifecycleScope.launch {
            try {
                val currentUsername = prefManager.getUsername()
                val currentAvatar = prefManager.getAvatar()
                val currentUserId = prefManager.getUserId()

                val newChat = ChatSend(
                    userId1 = currentUserId,
                    userId2 = user._id,
                    avatar1 = currentAvatar,
                    avatar2 = user.profile.avatar,
                    username1 = currentUsername,
                    username2 = user.username,
                    messages = emptyList()
                )
                apiService.createChats(newChat)
                Toast.makeText(this@AddChatActivity, "Chat created successfully", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AddChatActivity, "Failed to create chat", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
