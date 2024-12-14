package com.example.uas_synthesize.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedThreadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedThread(thread: LikedThreadEntity)

    @Query("SELECT * FROM LikedThreadEntity")
    fun getAllLikedThreads(): Flow<List<LikedThreadEntity>>

    @Query("DELETE FROM LikedThreadEntity WHERE _id = :threadId")
    suspend fun deleteLikedThread(threadId: String)

    @Query("DELETE FROM LikedThreadEntity")
    suspend fun deleteAllLikedThread()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("SELECT * FROM CommentEntity WHERE threadId = :threadId")
    fun getCommentsByThread(threadId: String): Flow<List<CommentEntity>>

    @Query("DELETE FROM CommentEntity WHERE id = :commentId")
    suspend fun deleteComment(commentId: Int)

    @Query("DELETE FROM CommentEntity")
    suspend fun deleteAllComment()
}
