package com.example.vozi001.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vozi001.api.models.Session
import com.example.vozi001.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para operaciones de sesiones con API
 */
class SessionApiViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "SessionApiViewModel"
    }
    
    private val repository = SessionRepository()
    
    private val _uiState = MutableStateFlow(SessionApiUiState())
    val uiState: StateFlow<SessionApiUiState> = _uiState.asStateFlow()
    
    // Exponer las sesiones del repositorio
    val sessions = repository.sessions
    val isLoading = repository.isLoading
    
    /**
     * Carga todas las sesiones
     */
    fun loadAllSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            
            repository.loadAllSessions().onFailure { error ->
                Log.e(TAG, "Error al cargar sesiones: ${error.message}")
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }
    
    /**
     * Carga sesiones de un terapeuta
     */
    fun loadSessionsByTherapist(therapistId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            
            repository.loadSessionsByTherapist(therapistId).onFailure { error ->
                Log.e(TAG, "Error al cargar sesiones del terapeuta: ${error.message}")
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }
    
    /**
     * Carga sesiones de un niño
     */
    fun loadSessionsByChild(childId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            
            repository.loadSessionsByChild(childId).onFailure { error ->
                Log.e(TAG, "Error al cargar sesiones del niño: ${error.message}")
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }
    
    /**
     * Obtiene una sesión por ID
     */
    fun getSessionById(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, error = null) }
            
            repository.getSessionById(sessionId)
                .onSuccess { session ->
                    _uiState.update { 
                        it.copy(
                            currentSession = session,
                            isLoadingDetail = false
                        ) 
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error al obtener sesión: ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isLoadingDetail = false,
                            error = error.message
                        ) 
                    }
                }
        }
    }
    
    /**
     * Crea una nueva sesión
     */
    fun createSession(session: Session) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, error = null) }
            
            repository.createSession(session)
                .onSuccess { sessionId ->
                    Log.d(TAG, "Sesión creada: $sessionId")
                    _uiState.update { 
                        it.copy(
                            isCreating = false,
                            lastCreatedSessionId = sessionId
                        ) 
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error al crear sesión: ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isCreating = false,
                            error = error.message
                        ) 
                    }
                }
        }
    }
    
    /**
     * Actualiza una sesión existente
     */
    fun updateSession(session: Session) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }
            
            repository.updateSession(session)
                .onSuccess {
                    Log.d(TAG, "Sesión actualizada: ${session.id}")
                    _uiState.update { it.copy(isUpdating = false) }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error al actualizar sesión: ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isUpdating = false,
                            error = error.message
                        ) 
                    }
                }
        }
    }
    
    /**
     * Elimina una sesión
     */
    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }
            
            repository.deleteSession(sessionId)
                .onSuccess {
                    Log.d(TAG, "Sesión eliminada: $sessionId")
                    _uiState.update { it.copy(isDeleting = false) }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error al eliminar sesión: ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isDeleting = false,
                            error = error.message
                        ) 
                    }
                }
        }
    }
    
    /**
     * Marca una sesión como completada
     */
    fun markSessionAsCompleted(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            
            repository.markSessionAsCompleted(sessionId)
                .onSuccess {
                    Log.d(TAG, "Sesión marcada como completada: $sessionId")
                }
                .onFailure { error ->
                    Log.e(TAG, "Error al marcar sesión: ${error.message}")
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
    
    /**
     * Limpia el error actual
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    /**
     * Limpia la sesión actual
     */
    fun clearCurrentSession() {
        _uiState.update { it.copy(currentSession = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        repository.clearCache()
        Log.d(TAG, "ViewModel cleared")
    }
}

/**
 * Estado de UI para SessionApiViewModel
 */
data class SessionApiUiState(
    val currentSession: Session? = null,
    val isLoadingDetail: Boolean = false,
    val isCreating: Boolean = false,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val lastCreatedSessionId: String? = null
)
