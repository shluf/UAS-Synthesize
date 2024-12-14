package com.example.uas_synthesize.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val threadId: String,
    val author: String,
    val avatar: String,
    val content: String
)