package com.example.uas_synthesize.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LikedThreadEntity(
    @PrimaryKey val _id: String,
    val sender: String,
    val fullname: String,
    val avatar: String,
    val content: String,
    val likeCount: Int
)
