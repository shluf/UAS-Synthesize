package com.example.uas_synthesize.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.database.ChatDatabase
import com.example.uas_synthesize.data.local.ChatEntity
import com.example.uas_synthesize.data.local.MessageEntity
import com.example.uas_synthesize.data.model.get.Chat
import com.example.uas_synthesize.data.model.get.Message
import com.example.uas_synthesize.data.network.ApiClient
import com.example.uas_synthesize.databinding.FragmentChatBinding
import com.example.uas_synthesize.ui.main.chat.AddChatActivity
import com.example.uas_synthesize.ui.main.chat.ChatAdapter
import com.example.uas_synthesize.utils.PrefManager
import com.example.uas_synthesize.utils.SwipeToDeleteCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val apiService = ApiClient.getInstance()

    private lateinit var chatDatabase: ChatDatabase
    private lateinit var prefManager: PrefManager

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatDatabase = ChatDatabase.getInstance(requireContext())
        prefManager = PrefManager.getInstance(requireActivity())
        chatAdapter = ChatAdapter(prefManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter

//          Swipe Delete
            val swipeToDeleteCallback = SwipeToDeleteCallback(requireContext()) { position ->
                val chatToDelete = chatAdapter.getCurrentList()[position]
                showDeleteConfirmationDialog(chatToDelete, position)
            }

            ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(this)
        }
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if (!prefManager.isGuest()) {
                    getChats()
                    fetchChats()
                }
            }
        }

        binding.buttonAddChat.setOnClickListener {
            val intent = Intent(requireContext(), AddChatActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        if (!prefManager.isGuest()) {
            getChats()
            fetchChats()
        }
    }


    private fun fetchChats() {
        lifecycleScope.launch {
            try {
                val currentUsername = prefManager.getUsername()
                val chats = apiService.getChats().filter { it.username1 == currentUsername || it.username2 == currentUsername }

                chats.forEach { chat ->
                    chatDatabase.chatDao().insertChat(
                        ChatEntity(
                            id = chat._id,
                            userId1 = chat.userId1,
                            userId2 = chat.userId2,
                            avatar1 = chat.avatar1,
                            avatar2 = chat.avatar2,
                            username1 = chat.username1,
                            username2 = chat.username2
                        )
                    )

                    chatDatabase.chatDao().deleteAllMessagesByChat(chat._id)
                    chat.messages.forEach { message ->
//                        val existingMessage = chatDatabase.chatDao().getMessageById()
//                        if (existingMessage == null) {
                            chatDatabase.chatDao().insertMessage(
                                MessageEntity(
                                    chatId = chat._id,
                                    sender = message.sender,
                                    receiver = message.receiver,
                                    content = message.content,
                                    img = message.img
                                )
                            )
//                        }
                    }
                }

                getChats()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load chats", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun mapChatEntitiesToChats(chatEntities: List<ChatEntity>): List<Chat> {
        return chatEntities.map { chatEntity ->
            val messages = chatDatabase.chatDao().getMessagesForChat(chatEntity.id).map { messageEntity ->
                Message(
                    sender = messageEntity.sender,
                    receiver = messageEntity.receiver,
                    content = messageEntity.content,
                    img = messageEntity.img
                )
            }
            Chat(
                _id = chatEntity.id,
                userId1 = chatEntity.userId1,
                userId2 = chatEntity.userId2,
                avatar1 = chatEntity.avatar1,
                avatar2 = chatEntity.avatar2,
                username1 = chatEntity.username1,
                username2 = chatEntity.username2,
                messages = messages
            )
        }
    }

    private fun getChats() {
        lifecycleScope.launch {
            val localChatEntities = chatDatabase.chatDao().getAllChats()
            val localChats = mapChatEntitiesToChats(localChatEntities)

            chatAdapter.submitList(localChats)
        }
    }

    private fun showDeleteConfirmationDialog(chat: Chat, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Obrolan")
            .setMessage("Apakah Anda yakin ingin menghapus obrolan ini?")
            .setPositiveButton("Hapus") { _, _ ->

                lifecycleScope.launch {
                    // Hapus dari room
                    chatDatabase.chatDao().deleteChatById(chat._id)
                    chatDatabase.chatDao().deleteAllMessagesByChat(chat._id)

                    // Hapus dari adapter
                    withContext(Dispatchers.Main) {
                        chatAdapter.removeChat(position)

                        // Hapus dari api
                        try {
                            apiService.deleteChat(chat._id)
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Gagal menghapus obrolan dari server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal") { _, _ ->
                chatAdapter.notifyItemChanged(position)
            }
            .setOnCancelListener {
                chatAdapter.notifyItemChanged(position)
            }
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}