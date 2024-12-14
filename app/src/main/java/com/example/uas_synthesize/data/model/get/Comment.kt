package com.example.uas_synthesize.data.model.get

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val author: String,
    val avatar: String,
    val content: String,
) : Parcelable
