package com.example.vozi001.data.repository

import com.example.vozi001.data.db.Practice
import com.example.vozi001.data.db.PracticeDao
import com.example.vozi001.domain.repository.PracticeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del repositorio de prácticas
 */
class PracticeRepositoryImpl(
    private val practiceDao: PracticeDao
) : PracticeRepository {
    
    override suspend fun insertPractice(practice: Practice): Long {
        return try {
            practiceDao.insert(practice)
        } catch (e: Exception) {
            throw RepositoryException("Error al insertar práctica", e)
        }
    }
    
    override suspend fun updatePractice(practice: Practice) {
        try {
            practiceDao.update(practice)
        } catch (e: Exception) {
            throw RepositoryException("Error al actualizar práctica", e)
        }
    }
    
    override suspend fun deletePractice(practice: Practice) {
        try {
            practiceDao.delete(practice)
        } catch (e: Exception) {
            throw RepositoryException("Error al eliminar práctica", e)
        }
    }
    
    override suspend fun deletePracticeById(practiceId: Long) {
        try {
            practiceDao.deleteById(practiceId)
        } catch (e: Exception) {
            throw RepositoryException("Error al eliminar práctica por ID", e)
        }
    }
    
    override suspend fun getPracticeById(practiceId: Long): Practice? {
        return try {
            practiceDao.getPracticeById(practiceId)
        } catch (e: Exception) {
            throw RepositoryException("Error al obtener práctica", e)
        }
    }
    
    override fun getPracticesByUser(userId: String): Flow<List<Practice>> {
        return practiceDao.getPracticesByUser(userId)
    }
    
    override fun getAllPractices(): Flow<List<Practice>> {
        return practiceDao.getAllPractices()
    }
    
    override suspend fun getAverageStars(userId: String): Float {
        return try {
            practiceDao.getAverageStars(userId) ?: 0f
        } catch (e: Exception) {
            throw RepositoryException("Error al obtener promedio de estrellas", e)
        }
    }
    
    override suspend fun getPracticeCount(userId: String): Int {
        return try {
            practiceDao.getPracticeCount(userId)
        } catch (e: Exception) {
            throw RepositoryException("Error al contar prácticas", e)
        }
    }
    
    override suspend fun getRecentPractices(userId: String, limit: Int): List<Practice> {
        return try {
            practiceDao.getRecentPractices(userId, limit)
        } catch (e: Exception) {
            throw RepositoryException("Error al obtener prácticas recientes", e)
        }
    }
    
    override suspend fun deleteAllByUser(userId: String) {
        try {
            practiceDao.deleteAllByUser(userId)
        } catch (e: Exception) {
            throw RepositoryException("Error al eliminar todas las prácticas del usuario", e)
        }
    }
}

/**
 * Excepción personalizada para errores del repositorio
 */
class RepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)