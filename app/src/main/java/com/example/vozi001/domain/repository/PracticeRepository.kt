package com.example.vozi001.domain.repository

import com.example.vozi001.data.db.Practice
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de prácticas (Clean Architecture)
 */
interface PracticeRepository {
    
    /**
     * Inserta una nueva práctica
     */
    suspend fun insertPractice(practice: Practice): Long
    
    /**
     * Actualiza una práctica existente
     */
    suspend fun updatePractice(practice: Practice)
    
    /**
     * Elimina una práctica
     */
    suspend fun deletePractice(practice: Practice)
    
    /**
     * Elimina una práctica por ID
     */
    suspend fun deletePracticeById(practiceId: Long)
    
    /**
     * Obtiene una práctica por ID
     */
    suspend fun getPracticeById(practiceId: Long): Practice?
    
    /**
     * Obtiene todas las prácticas de un usuario
     */
    fun getPracticesByUser(userId: String): Flow<List<Practice>>
    
    /**
     * Obtiene todas las prácticas
     */
    fun getAllPractices(): Flow<List<Practice>>
    
    /**
     * Obtiene el promedio de estrellas de un usuario
     */
    suspend fun getAverageStars(userId: String): Float
    
    /**
     * Obtiene el total de prácticas de un usuario
     */
    suspend fun getPracticeCount(userId: String): Int
    
    /**
     * Obtiene las prácticas recientes de un usuario
     */
    suspend fun getRecentPractices(userId: String, limit: Int): List<Practice>
    
    /**
     * Elimina todas las prácticas de un usuario
     */
    suspend fun deleteAllByUser(userId: String)
}
