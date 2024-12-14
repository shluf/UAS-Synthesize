package com.example.uas_synthesize.data.model.get

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val sender: String,
    val receiver: String,
    val content: String,
    val img: String?,
) : Parcelable
