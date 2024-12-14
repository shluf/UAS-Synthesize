package com.example.uas_synthesize.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: String,
    val sender: String,
    val receiver: String,
    val content: String,
    val img: String?
)