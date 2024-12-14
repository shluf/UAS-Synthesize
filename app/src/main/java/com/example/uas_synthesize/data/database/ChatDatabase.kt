package com.example.uas_synthesize.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uas_synthesize.data.local.ChatDao
import com.example.uas_synthesize.data.local.ChatEntity
import com.example.uas_synthesize.data.local.MessageEntity

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getInstance(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
