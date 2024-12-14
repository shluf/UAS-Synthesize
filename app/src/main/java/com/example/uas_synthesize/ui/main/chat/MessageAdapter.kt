package com.example.uas_synthesize.ui.main.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_synthesize.data.model.get.Message
import com.example.uas_synthesize.databinding.ItemMessageReceivedBinding
import com.example.uas_synthesize.databinding.ItemMessageSentBinding

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    private var currentUser: String = ""

    fun submitList(list: List<Message>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    fun setCurrentUser(username: String) {
        currentUser = username
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == currentUser) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textViewMessage.text = message.content
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textViewMessage.text = message.content
            binding.textViewSenderName.text = message.sender
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}