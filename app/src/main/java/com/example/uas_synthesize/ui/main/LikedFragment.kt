package com.example.uas_synthesize.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.database.LikedThreadDatabase
import com.example.uas_synthesize.data.local.CommentEntity
import com.example.uas_synthesize.data.local.LikedThreadDao
import com.example.uas_synthesize.data.model.get.Comment
import com.example.uas_synthesize.data.model.get.ThreadPost
import com.example.uas_synthesize.ui.main.thread.CommentActivity
import com.example.uas_synthesize.ui.main.thread.ThreadAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LikedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LikedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var likedThreadAdapter: ThreadAdapter
    private val likedThreads = mutableListOf<ThreadPost>()

    private lateinit var likedThreadDatabase: LikedThreadDatabase
    private lateinit var likedThreadDao: LikedThreadDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        likedThreadDatabase = LikedThreadDatabase.getInstance(requireContext())
        likedThreadDao = likedThreadDatabase.likedThreadDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liked, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewLikedThreads)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likedThreadAdapter = ThreadAdapter(likedThreads,
            onThreadClick = { thread ->
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("thread", thread)
                startActivity(intent)
            },
            onLikeClick = { thread ->
                unlikeThread(thread)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = likedThreadAdapter

        fetchLikedThreads()
    }

    private fun fetchLikedThreads() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val likedThreadEntities = likedThreadDao.getAllLikedThreads().firstOrNull() ?: emptyList()

                val threads = likedThreadEntities.map { entity ->
                    ThreadPost(
                        _id = entity._id,
                        sender = entity.sender,
                        fullname = entity.fullname,
                        avatar = entity.avatar,
                        content = entity.content,
                        likeCount = entity.likeCount,
                        comments = fetchComments(entity._id)
                    )
                }

                likedThreads.clear()
                likedThreads.addAll(threads)

                // Update UI on main thread
                CoroutineScope(Dispatchers.Main).launch {
                    likedThreadAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Error loading liked threads", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchComments(threadId: String): List<Comment> {
        return runBlocking {
            // Fetch CommentEntity from the database
            val commentEntities = likedThreadDao.getCommentsByThread(threadId).firstOrNull() ?: emptyList()

            // Map CommentEntity to Comment
            commentEntities.map { entity ->
                Comment(
                    author = entity.author,
                    avatar = entity.avatar,
                    content = entity.content
                )
            }
        }
    }

    private fun unlikeThread(thread: ThreadPost) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Delete from Room
                likedThreadDao.deleteLikedThread(thread._id)

                // Remove from list
                likedThreads.remove(thread)

                CoroutineScope(Dispatchers.Main).launch {
                    likedThreadAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Unliked thread", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Error unliking thread", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment LikedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LikedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}