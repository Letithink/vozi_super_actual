package com.example.vozi001.domain.use_case

import com.example.vozi001.data.db.Practice
import com.example.vozi001.domain.repository.PracticeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case para obtener el historial de prácticas de un usuario
 */
class GetPracticeHistoryUseCase(
    private val repository: PracticeRepository
) {
    
    /**
     * Obtiene el historial completo de prácticas de un usuario
     * 
     * @param userId ID del usuario
     * @return Flow con lista de prácticas ordenadas por fecha descendente
     */
    operator fun invoke(userId: String): Flow<List<Practice>> {
        require(userId.isNotBlank()) { "El ID de usuario no puede estar vacío" }
        return repository.getPracticesByUser(userId)
    }
    
    /**
     * Obtiene las últimas N prácticas de un usuario
     * 
     * @param userId ID del usuario
     * @param limit Número máximo de prácticas a retornar
     * @return Lista de prácticas recientes
     */
    suspend fun getRecent(userId: String, limit: Int = 10): Result<List<Practice>> {
        return try {
            require(userId.isNotBlank()) { "El ID de usuario no puede estar vacío" }
            require(limit > 0) { "El límite debe ser mayor a 0" }
            
            val practices = repository.getRecentPractices(userId, limit)
            Result.success(practices)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener prácticas recientes: ${e.message}", e))
        }
    }
}
