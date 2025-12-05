package com.example.vozi001.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operaciones de base de datos con prácticas
 */
@Dao
interface PracticeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(practice: Practice): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(practices: List<Practice>)
    
    @Update
    suspend fun update(practice: Practice)
    
    @Delete
    suspend fun delete(practice: Practice)
    
    @Query("DELETE FROM practices WHERE id = :practiceId")
    suspend fun deleteById(practiceId: Long)
    
    @Query("SELECT * FROM practices WHERE id = :practiceId")
    suspend fun getPracticeById(practiceId: Long): Practice?
    
    @Query("SELECT * FROM practices WHERE userId = :userId ORDER BY timestamp DESC")
    fun getPracticesByUser(userId: String): Flow<List<Practice>>
    
    @Query("SELECT * FROM practices ORDER BY timestamp DESC")
    fun getAllPractices(): Flow<List<Practice>>
    
    @Query("SELECT * FROM practices WHERE userId = :userId AND timestamp >= :startTime ORDER BY timestamp DESC")
    fun getPracticesByUserSince(userId: String, startTime: Long): Flow<List<Practice>>
    
    @Query("SELECT AVG(stars) FROM practices WHERE userId = :userId")
    suspend fun getAverageStars(userId: String): Float?
    
    @Query("SELECT COUNT(*) FROM practices WHERE userId = :userId")
    suspend fun getPracticeCount(userId: String): Int
    
    @Query("SELECT * FROM practices WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentPractices(userId: String, limit: Int): List<Practice>
    
    @Query("DELETE FROM practices WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)
}
