package com.example.uas_synthesize.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_synthesize.R
import com.example.uas_synthesize.data.model.get.Comment

class CommentAdapter(
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val content: TextView = itemView.findViewById(R.id.txtCommentContent)
        val author: TextView = itemView.findViewById(R.id.txtCommentAuthor)
        val divider: View = itemView.findViewById(R.id.verticalDivider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.author.text = comment.content
        holder.content.text = "~${comment.author}"

        Glide.with(holder.itemView.context)
            .load(comment.avatar)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_error_avatar)
            .into(holder.avatar)

        if (position == comments.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = comments.size
}