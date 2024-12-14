package com.example.uas_synthesize.data.model.get

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreadPost(
    val _id: String,
    val sender: String,
    val fullname: String,
    val avatar: String,
    val content: String,
    val likeCount: Int,
    val comments: List<Comment>
) : Parcelable