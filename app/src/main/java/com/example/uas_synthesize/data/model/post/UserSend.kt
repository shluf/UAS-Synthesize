package com.example.uas_synthesize.data.model.post

import com.example.uas_synthesize.data.model.get.Profile

data class UserSend(
    val username: String,
    val password: String,
    val profile: Profile?,
)