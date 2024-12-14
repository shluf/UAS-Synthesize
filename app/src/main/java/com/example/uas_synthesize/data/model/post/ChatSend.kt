package com.example.uas_synthesize.data.model.post

import com.example.uas_synthesize.data.model.get.Message

data class ChatSend (
    val userId1: String,
    val userId2: String,
    val avatar1: String,
    val avatar2: String,
    val username1: String,
    val username2: String,
    val messages: List<Message>
)