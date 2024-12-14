package com.example.uas_synthesize.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val userId1: String,
    val userId2: String,
    val avatar1: String,
    val avatar2: String,
    val username1: String,
    val username2: String
)