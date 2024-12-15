package com.example.uas_synthesize.ui.main.chat

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uas_synthesize.R

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.uas_synthesize.data.model.get.Chat
import com.example.uas_synthesize.data.model.get.Comment
import com.example.uas_synthesize.data.model.get.Message
import com.example.uas_synthesize.data.model.get.ThreadPost
import com.example.uas_synthesize.data.model.post.ChatSend
import com.example.uas_synthesize.data.network.ApiClient
import com.example.uas_synthesize.databinding.ActivityChatContentBinding
import com.example.uas_synthesize.utils.PrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ChatContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatContentBinding
    private val messageAdapter = MessageAdapter()

    private lateinit var chat: Chat
    private lateinit var prefManager: PrefManager

    private val apiService = ApiClient.getInstance()

    private lateinit var currentUsername: String
    private lateinit var receiver: String
    private lateinit var receiverAvatar: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)
        currentUsername = prefManager.getUsername()

        chat = intent.getParcelableExtra("chat")!!


        receiver = if (chat.username2 == currentUsername) chat.username1 else chat.username2
        receiverAvatar = if (chat.username2 == currentUsername) chat.avatar1 else chat.avatar2

        setupRecyclerView()
        displayChatData()

        binding.tvName.text = receiver

        Glide.with(this)
            .load(receiverAvatar)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_error_avatar)
            .into(binding.ivUserAvatar)

        binding.buttonSend.setOnClickListener {
            val messageContent = binding.editTextMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                sendMessage(messageContent)
                binding.editTextMessage.text?.clear()
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatContentActivity)
            adapter = messageAdapter
        }
    }

    private fun displayChatData() {
        messageAdapter.setCurrentUser(currentUsername)
        messageAdapter.submitList(chat.messages)
    }

    private fun sendMessage(content: String) {
        val newMessage = Message(
            sender = currentUsername,
            receiver = receiver,
            content = content,
            img = null
        )

        val updatedMessages = chat.messages.toMutableList().apply { add(newMessage) }
        val updatedChat = chat.copy(messages = updatedMessages)

        lifecycleScope.launch {
            try {
                apiService.updateChat(chat._id, updatedChat)

                chat = updatedChat
                messageAdapter.submitList(updatedMessages)
                Toast.makeText(this@ChatContentActivity, "Message sent", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ChatContentActivity, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
