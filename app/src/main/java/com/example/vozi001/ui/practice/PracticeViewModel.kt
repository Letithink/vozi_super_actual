package com.example.vozi001.ui.practice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vozi001.data.db.AppDatabase
import com.example.vozi001.data.db.Practice
import com.example.vozi001.data.repository.PracticeRepositoryImpl
import com.example.vozi001.domain.use_case.CreatePracticeUseCase
import com.example.vozi001.domain.use_case.GetPracticeHistoryUseCase
import com.example.vozi001.utils.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar estados de práctica
 */
class PracticeViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "PracticeViewModel"
    }
    
    private val database = AppDatabase.getInstance(application)
    private val repository = PracticeRepositoryImpl(database.practiceDao())
    private val createPracticeUseCase = CreatePracticeUseCase(repository)
    private val getPracticeHistoryUseCase = GetPracticeHistoryUseCase(repository)
    
    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()
    
    init {
        loadPracticeHistory()
    }
    
    /**
     * Carga el historial de prácticas del usuario actual
     */
    fun loadPracticeHistory() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val userId = UserManager.getCurrentUserUid()
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Usuario no autenticado"
                        ) 
                    }
                    return@launch
                }
                
                getPracticeHistoryUseCase(userId).collect { practices ->
                    _uiState.update { 
                        it.copy(
                            practices = practices,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar historial: ${e.message}")
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Error al cargar historial: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Crea una nueva práctica
     */
    fun createPractice(
        targetWord: String,
        heardWord: String,
        stars: Int,
        duration: Long = 0L,
        accuracy: Float = 0f
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCreating = true, error = null) }
                
                val userId = UserManager.getCurrentUserUid()
                if (userId == null) {
                    _uiState.update { 
                        it.copy(
                            isCreating = false, 
                            error = "Usuario no autenticado"
                        ) 
                    }
                    return@launch
                }
                
                val result = createPracticeUseCase(
                    userId = userId,
                    targetWord = targetWord,
                    heardWord = heardWord,
                    stars = stars,
                    duration = duration,
                    accuracy = accuracy
                )
                
                result.onSuccess { practiceId ->
                    Log.d(TAG, "Práctica creada con ID: $practiceId")
                    _uiState.update { 
                        it.copy(
                            isCreating = false,
                            lastCreatedPracticeId = practiceId,
                            error = null
                        ) 
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Error al crear práctica: ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isCreating = false, 
                            error = error.message
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado: ${e.message}")
                _uiState.update { 
                    it.copy(
                        isCreating = false, 
                        error = "Error inesperado: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Carga estadísticas del usuario
     */
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                val userId = UserManager.getCurrentUserUid() ?: return@launch
                
                val averageStars = repository.getAverageStars(userId)
                val practiceCount = repository.getPracticeCount(userId)
                
                _uiState.update { 
                    it.copy(
                        averageStars = averageStars,
                        totalPractices = practiceCount
                    ) 
                }
                
                Log.d(TAG, "Estadísticas cargadas: avg=$averageStars, total=$practiceCount")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar estadísticas: ${e.message}")
            }
        }
    }
    
    /**
     * Limpia el error actual
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
    }
}

/**
 * Estado de UI para PracticeViewModel
 * Nota: PracticeUiState.kt ya existe, esta es una extensión
 */
data class PracticeUiState(
    val practices: List<Practice> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null,
    val lastCreatedPracticeId: Long? = null,
    val averageStars: Float = 0f,
    val totalPractices: Int = 0
)
