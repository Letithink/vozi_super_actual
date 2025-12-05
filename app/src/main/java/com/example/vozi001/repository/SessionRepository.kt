package com.example.vozi001.repository

import android.util.Log
import com.example.vozi001.api.SessionApiService
import com.example.vozi001.api.models.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio de sesiones que integra API y caché local
 */
class SessionRepository(
    private val apiService: SessionApiService = SessionApiService()
) {
    
    companion object {
        private const val TAG = "SessionRepository"
    }
    
    // Caché local de sesiones
    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Carga todas las sesiones desde la API
     */
    suspend fun loadAllSessions(): Result<Unit> {
        _isLoading.value = true
        return try {
            val result = apiService.getAllSessions()
            result.onSuccess { sessionsList ->
                _sessions.value = sessionsList
                Log.d(TAG, "Sesiones cargadas: ${sessionsList.size}")
            }
            result.map { }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar sesiones: ${e.message}")
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Carga sesiones de un terapeuta
     */
    suspend fun loadSessionsByTherapist(therapistId: String): Result<Unit> {
        _isLoading.value = true
        return try {
            val result = apiService.getSessionsByTherapist(therapistId)
            result.onSuccess { sessionsList ->
                _sessions.value = sessionsList
            }
            result.map { }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar sesiones del terapeuta: ${e.message}")
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Carga sesiones de un niño
     */
    suspend fun loadSessionsByChild(childId: String): Result<Unit> {
        _isLoading.value = true
        return try {
            val result = apiService.getSessionsByChild(childId)
            result.onSuccess { sessionsList ->
                _sessions.value = sessionsList
            }
            result.map { }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar sesiones del niño: ${e.message}")
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Obtiene una sesión por ID
     */
    suspend fun getSessionById(sessionId: String): Result<Session> {
        // Primero buscar en caché
        val cachedSession = _sessions.value.find { it.id == sessionId }
        if (cachedSession != null) {
            return Result.success(cachedSession)
        }
        
        // Si no está en caché, buscar en API
        return apiService.getSessionById(sessionId)
    }
    
    /**
     * Crea una nueva sesión
     */
    suspend fun createSession(session: Session): Result<String> {
        return try {
            val result = apiService.createSession(session)
            result.onSuccess { sessionId ->
                // Agregar a caché
                val newSession = session.copy(id = sessionId)
                _sessions.value = listOf(newSession) + _sessions.value
                Log.d(TAG, "Sesión creada y agregada al caché")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza una sesión existente
     */
    suspend fun updateSession(session: Session): Result<Unit> {
        return try {
            val result = apiService.updateSession(session)
            result.onSuccess {
                // Actualizar caché
                _sessions.value = _sessions.value.map { 
                    if (it.id == session.id) session else it 
                }
                Log.d(TAG, "Sesión actualizada en caché")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Elimina una sesión
     */
    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            val result = apiService.deleteSession(sessionId)
            result.onSuccess {
                // Eliminar de caché
                _sessions.value = _sessions.value.filter { it.id != sessionId }
                Log.d(TAG, "Sesión eliminada del caché")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Marca una sesión como completada
     */
    suspend fun markSessionAsCompleted(sessionId: String): Result<Unit> {
        return try {
            val result = apiService.markSessionAsCompleted(sessionId)
            result.onSuccess {
                // Actualizar caché
                _sessions.value = _sessions.value.map { session ->
                    if (session.id == sessionId) {
                        session.copy(isCompleted = true)
                    } else {
                        session
                    }
                }
                Log.d(TAG, "Sesión marcada como completada en caché")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error al marcar sesión como completada: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Limpia el caché de sesiones
     */
    fun clearCache() {
        _sessions.value = emptyList()
        Log.d(TAG, "Caché limpiado")
    }
}
