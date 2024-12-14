package com.example.uas_synthesize.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.model.get.ThreadPost
import com.google.android.material.button.MaterialButton

class ThreadAdapter(
    private val threads: List<ThreadPost>,
    private val onThreadClick: (ThreadPost) -> Unit,
    private val onLikeClick: (ThreadPost) -> Unit
) : RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder>() {

    inner class ThreadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvSender: TextView = view.findViewById(R.id.tvSender)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubTitle)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val btnLike: MaterialButton = view.findViewById(R.id.btnLike)
        val btnComment: MaterialButton = view.findViewById(R.id.btnComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thread, parent, false)
        return ThreadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        val thread = threads[position]

        holder.tvSender.text = thread.sender
        holder.tvSubtitle.text = thread.fullname
        holder.tvContent.text = thread.content
        holder.btnLike.text = "${thread.likeCount}"
        holder.btnComment.text = "${thread.comments.size}"

        Glide.with(holder.itemView.context)
            .load(thread.avatar)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_error_avatar)
            .into(holder.ivAvatar)

        holder.itemView.setOnClickListener {
            onThreadClick(thread)
        }

        holder.btnLike.setOnClickListener {
            onLikeClick(thread)
        }
    }

    override fun getItemCount(): Int = threads.size
}