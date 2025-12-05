package com.example.vozi001.domain.use_case

import com.example.vozi001.data.db.Practice
import com.example.vozi001.domain.repository.PracticeRepository

/**
 * Use Case para crear una nueva práctica
 */
class CreatePracticeUseCase(
    private val repository: PracticeRepository
) {
    
    /**
     * Ejecuta el caso de uso para crear una práctica
     * 
     * @param userId ID del usuario
     * @param targetWord Palabra objetivo
     * @param heardWord Palabra escuchada
     * @param stars Estrellas obtenidas (1-5)
     * @param duration Duración de la práctica en milisegundos
     * @param accuracy Precisión (0.0 - 1.0)
     * @return ID de la práctica creada
     */
    suspend operator fun invoke(
        userId: String,
        targetWord: String,
        heardWord: String,
        stars: Int,
        duration: Long = 0L,
        accuracy: Float = 0f
    ): Result<Long> {
        return try {
            // Validaciones
            require(userId.isNotBlank()) { "El ID de usuario no puede estar vacío" }
            require(targetWord.isNotBlank()) { "La palabra objetivo no puede estar vacía" }
            require(stars in 1..5) { "Las estrellas deben estar entre 1 y 5" }
            require(accuracy in 0f..1f) { "La precisión debe estar entre 0.0 y 1.0" }
            
            val practice = Practice(
                userId = userId,
                targetWord = targetWord,
                heardWord = heardWord,
                stars = stars,
                timestamp = System.currentTimeMillis(),
                duration = duration,
                accuracy = accuracy
            )
            
            val id = repository.insertPractice(practice)
            Result.success(id)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear la práctica: ${e.message}", e))
        }
    }
}
