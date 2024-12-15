package com.example.uas_synthesize.ui.main.chat

import com.example.uas_synthesize.data.model.get.User
import com.example.uas_synthesize.databinding.ItemUserBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_synthesize.R

class UserAdapter(
    private val userList: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.username
            binding.tvFullName.text = user.profile.name

            Glide.with(itemView.context)
                .load(user.profile.avatar)
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_error_avatar)
                .into(binding.ivUser)

            binding.root.setOnClickListener {
                onUserClick(user)
            }
        }
    }
}
