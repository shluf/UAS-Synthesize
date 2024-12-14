package com.example.uas_synthesize.data.network

import com.example.uas_synthesize.data.model.get.Chat
import com.example.uas_synthesize.data.model.get.Profile
import com.example.uas_synthesize.data.model.get.ThreadPost
import com.example.uas_synthesize.data.model.post.ThreadPostSend
import com.example.uas_synthesize.data.model.get.User
import com.example.uas_synthesize.data.model.post.ChatSend
import com.example.uas_synthesize.data.model.post.UserSend
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServices {
//  AUTH
    @GET("users/")
    suspend fun getUsers(): List<User>

    @POST("users/")
    suspend fun registerUser(@Body user: UserSend)

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): User

    @POST("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: UserSend
    )

//  CHAT
    @GET("chats")
    suspend fun getChats(): List<Chat>

    @POST("chats")
    suspend fun createChats(@Body chats: ChatSend)

    @POST("chats/{chatId}")
    suspend fun updateChat(
        @Path("chatId") chatId: String,
        @Body updatedChat: Chat
    )

    @DELETE("chats/{chatId}")
    suspend fun deleteChat(
        @Path("chatId") chatId: String
    )

//  THREADS
    @GET("threads")
    suspend fun getThreads(): List<ThreadPost>

    @POST("threads")
    suspend fun createThread(@Body thread: ThreadPostSend)

    @GET("threads/{threadId}")
    suspend fun getThreadDetail(
        @Path("threadId") threadId: String
    ): ThreadPost

    @POST("threads/{threadId}")
    suspend fun updateThread(
        @Path("threadId") threadId: String,
        @Body updatedThread: ThreadPost
    ): ThreadPost
}
