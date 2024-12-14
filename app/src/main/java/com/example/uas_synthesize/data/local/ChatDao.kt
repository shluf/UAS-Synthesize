package com.example.uas_synthesize.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM ChatEntity")
    suspend fun getAllChats(): List<ChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("SELECT * FROM ChatEntity WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Query("DELETE FROM ChatEntity WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Query("SELECT * FROM MessageEntity WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("SELECT * FROM MessageEntity WHERE chatId = :chatId")
    suspend fun getMessagesForChat(chatId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM ChatEntity")
    suspend fun deleteAllChats()

    @Query("DELETE FROM MessageEntity")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM MessageEntity WHERE chatId = :chatId")
    suspend fun deleteAllMessagesByChat(chatId: String)
}
