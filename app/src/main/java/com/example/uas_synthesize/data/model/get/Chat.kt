package com.example.uas_synthesize.data.model.get

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat (
    val _id: String,
    val userId1: String,
    val userId2: String,
    val avatar1: String,
    val avatar2: String,
    val username1: String,
    val username2: String,
    val messages: List<Message>
) : Parcelable