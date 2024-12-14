package com.example.uas_synthesize.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.database.LikedThreadDatabase
import com.example.uas_synthesize.data.local.CommentEntity
import com.example.uas_synthesize.data.local.LikedThreadEntity
import com.example.uas_synthesize.data.model.get.ThreadPost
import com.example.uas_synthesize.data.model.post.ThreadPostSend
import com.example.uas_synthesize.data.network.ApiClient
import com.example.uas_synthesize.ui.main.thread.CommentActivity
import com.example.uas_synthesize.ui.main.thread.ThreadAdapter
import com.example.uas_synthesize.utils.PrefManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ThreadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ThreadFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var threadAdapter: ThreadAdapter
    private val threads = mutableListOf<ThreadPost>()

    private lateinit var btnAddThread: FloatingActionButton
    private lateinit var layoutAddThread: LinearLayout
    private lateinit var edtThreadContent: EditText

    private val apiService = ApiClient.getInstance()
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_thread, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewThreads)
        btnAddThread = view.findViewById(R.id.btnAddThread)
        layoutAddThread = view.findViewById(R.id.layoutAddThread)
        edtThreadContent = view.findViewById(R.id.edtThreadContent)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        threadAdapter = ThreadAdapter(threads,
            onThreadClick = { thread ->
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("thread", thread)
                startActivity(intent)
            },
            onLikeClick = { thread ->
                likeThread(thread)
            }
        )

        if (prefManager.isGuest()) {
            layoutAddThread.visibility = View.GONE
        } else {
            layoutAddThread.visibility = View.VISIBLE
        }


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = threadAdapter

        btnAddThread.setOnClickListener {
            val content = edtThreadContent.text.toString()
            if (content.isNotEmpty()) {
                addThread(content)
                edtThreadContent.text.clear()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Thread content cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fetchThreads()
    }

    private fun fetchThreads() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getThreads()
                threads.clear()
                threads.addAll(response)
                CoroutineScope(Dispatchers.Main).launch {
                    threadAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Error fetching threads", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addThread(content: String) {
        val newThread = ThreadPostSend(
            sender = prefManager.getUsername(),
            fullname = prefManager.getName(),
            avatar = prefManager.getAvatar(),
            content = content,
            likeCount = 0,
            comments = emptyList()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.createThread(newThread)
                CoroutineScope(Dispatchers.Main).launch {
                    fetchThreads()
                    Toast.makeText(requireContext(), "Thread added", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Error adding thread", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun likeThread(thread: ThreadPost) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = LikedThreadDatabase.getInstance(requireContext())
            val likedThreadDao = database.likedThreadDao()

            val likedThreads = likedThreadDao.getAllLikedThreads()
                .firstOrNull()?.find { it._id == thread._id }

            if (likedThreads != null) {
//                Jika sudah di-like, hapus dari database dan kurangi likeCount di API
                likedThreadDao.deleteLikedThread(thread._id)

                val newLikeCount = thread.likeCount - 1
                val updatedThread = thread.copy(likeCount = newLikeCount)

                updateThreadLikeCount(thread._id, updatedThread)
            } else {
//                Jika belum di-like, tambahkan ke database dan tambah likeCount di API
                val threadEntity = LikedThreadEntity(
                    _id = thread._id,
                    sender = thread.sender,
                    fullname = thread.fullname,
                    avatar = thread.avatar,
                    content = thread.content,
                    likeCount = thread.likeCount + 1
                )
                likedThreadDao.insertLikedThread(threadEntity)

                thread.comments.forEach { comment ->
                    val commentEntity = CommentEntity(
                        threadId = thread._id,
                        avatar = comment.avatar,
                        author = comment.author,
                        content = comment.content
                    )
                    likedThreadDao.insertComment(commentEntity)
                }

                val newLikeCount = thread.likeCount + 1
                val updatedThread = thread.copy(likeCount = newLikeCount)

                updateThreadLikeCount(thread._id, updatedThread)
            }
        }
    }

    private fun updateThreadLikeCount(threadId: String, updatedThread: ThreadPost) {
        val position = threads.indexOfFirst { it._id == threadId }
        if (position != -1) {
            threads[position] = updatedThread
            CoroutineScope(Dispatchers.Main).launch {
                threadAdapter.notifyItemChanged(position)
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    apiService.updateThread(threadId, updatedThread)
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            requireContext(),
                            "Error updating like count on API",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThreadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThreadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}