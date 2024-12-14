package com.example.uas_synthesize.ui.main.chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.local.ChatEntity
import com.example.uas_synthesize.data.model.get.Chat
import com.example.uas_synthesize.databinding.ItemChatBinding
import com.example.uas_synthesize.utils.PrefManager

class ChatAdapter(prefManager: PrefManager) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val chatList = mutableListOf<Chat>()
    private val currentUsername = prefManager.getUsername()

    fun submitList(list: List<Chat>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeChat(position: Int) {
        val currentList = chatList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

    fun getCurrentList(): List<Chat> {
        return chatList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            val title: String
            val avatar: String

            if (chat.username2 == currentUsername) {
                title = chat.username1
                avatar = chat.avatar1
            } else {
                title = chat.username2
                avatar = chat.avatar2
            }

            Glide.with(binding.root.context)
                .load(avatar)
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_error_avatar)
                .into(binding.ivAvatar)
            binding.textViewChatName.text = title
            binding.tvMessageCount.text = "${chat.messages.size} pesan"
            binding.textViewLastMessage.text = chat.messages.lastOrNull()?.content ?: "Tidak ada pesan"

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ChatContentActivity::class.java).apply {
                    putExtra("chat", chat)
                }
                context.startActivity(intent)
            }
        }
    }
}
