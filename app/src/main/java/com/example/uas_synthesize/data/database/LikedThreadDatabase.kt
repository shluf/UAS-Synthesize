package com.example.uas_synthesize.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uas_synthesize.data.local.CommentEntity
import com.example.uas_synthesize.data.local.LikedThreadDao
import com.example.uas_synthesize.data.local.LikedThreadEntity

@Database(entities = [LikedThreadEntity::class, CommentEntity::class], version = 1)
abstract class LikedThreadDatabase : RoomDatabase() {
    abstract fun likedThreadDao(): LikedThreadDao

    companion object {
        @Volatile
        private var INSTANCE: LikedThreadDatabase? = null

        fun getInstance(context: Context): LikedThreadDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LikedThreadDatabase::class.java,
                    "liked_thread_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
