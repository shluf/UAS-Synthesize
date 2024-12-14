package com.example.uas_synthesize.data.model.post

import com.example.uas_synthesize.data.model.get.Comment

data class ThreadPostSend(
    val sender: String,
    val fullname: String,
    val avatar: String,
    val content: String,
    val likeCount: Int,
    val comments: List<Comment>
)
