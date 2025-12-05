package com.example.vozi001.domain.use_case

import com.example.vozi001.data.db.Practice
import com.example.vozi001.domain.repository.PracticeRepository

/**
 * Use Case para actualizar una práctica existente
 */
class UpdatePracticeUseCase(
    private val repository: PracticeRepository
) {
    
    /**
     * Actualiza una práctica existente
     * 
     * @param practice Práctica a actualizar
     * @return Result indicando éxito o error
     */
    suspend operator fun invoke(practice: Practice): Result<Unit> {
        return try {
            // Validaciones
            require(practice.id > 0) { "El ID de la práctica debe ser válido" }
            require(practice.userId.isNotBlank()) { "El ID de usuario no puede estar vacío" }
            require(practice.targetWord.isNotBlank()) { "La palabra objetivo no puede estar vacía" }
            require(practice.stars in 1..5) { "Las estrellas deben estar entre 1 y 5" }
            require(practice.accuracy in 0f..1f) { "La precisión debe estar entre 0.0 y 1.0" }
            
            // Verificar que la práctica existe
            val existingPractice = repository.getPracticeById(practice.id)
            require(existingPractice != null) { "La práctica con ID ${practice.id} no existe" }
            
            repository.updatePractice(practice)
            Result.success(Unit)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar la práctica: ${e.message}", e))
        }
    }
    
    /**
     * Actualiza solo las estrellas y precisión de una práctica
     */
    suspend fun updateScore(practiceId: Long, stars: Int, accuracy: Float): Result<Unit> {
        return try {
            require(practiceId > 0) { "El ID de la práctica debe ser válido" }
            require(stars in 1..5) { "Las estrellas deben estar entre 1 y 5" }
            require(accuracy in 0f..1f) { "La precisión debe estar entre 0.0 y 1.0" }
            
            val practice = repository.getPracticeById(practiceId)
                ?: return Result.failure(Exception("Práctica no encontrada"))
            
            val updated = practice.copy(stars = stars, accuracy = accuracy)
            repository.updatePractice(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar puntuación: ${e.message}", e))
        }
    }
}
