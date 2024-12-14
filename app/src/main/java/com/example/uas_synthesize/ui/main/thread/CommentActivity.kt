package com.example.uas_synthesize.ui.main.thread

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.model.get.Comment
import com.example.uas_synthesize.data.model.get.ThreadPost
import com.example.uas_synthesize.data.network.ApiClient
import com.example.uas_synthesize.databinding.ActivityCommentBinding
import com.example.uas_synthesize.utils.PrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter

    private lateinit var binding: ActivityCommentBinding

    private lateinit var thread: ThreadPost
    private val comments = mutableListOf<Comment>()

    private lateinit var prefManager: PrefManager

    private val apiService = ApiClient.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        thread = intent.getParcelableExtra("thread")!!

        if (prefManager.isGuest()) {
            binding.layoutAddComment.visibility = View.GONE
        } else {
            binding.layoutAddComment.visibility = View.VISIBLE
        }


        recyclerView = binding.recyclerViewComments

        commentAdapter = CommentAdapter(comments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = commentAdapter

        comments.addAll(thread.comments)
        commentAdapter.notifyDataSetChanged()

        Glide.with(this)
            .load(thread.avatar)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_error_avatar)
            .into(binding.ivAvatar)

        with(binding) {
            threadContent.text = thread.content
            tvTitle.text = thread.sender
            tvSubTitle.text = thread.fullname
            tvComment.text = "Comments  (${thread.comments.size})"
            btnAddComment.setOnClickListener {
                val commentContent = edtComment.text.toString()
                if (commentContent.isNotEmpty()) {
                    addComment(commentContent)
                    edtComment.text.clear()
                } else {
                    Toast.makeText(
                        this@CommentActivity,
                        "Comment cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addComment(content: String) {
        val currentUsername = prefManager.getUsername()
        val currentAvatar = prefManager.getAvatar()
        val newComment = Comment(content = content, author = currentUsername, avatar = currentAvatar)
        val updatedComments = thread.comments.toMutableList().apply { add(newComment) }
        val updatedThread = thread.copy(comments = updatedComments)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.updateThread(thread._id, updatedThread)
                CoroutineScope(Dispatchers.Main).launch {
                    comments.add(newComment)
                    commentAdapter.notifyItemInserted(comments.size - 1)
                    Toast.makeText(this@CommentActivity, "Comment added", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@CommentActivity, "Error adding comment", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}