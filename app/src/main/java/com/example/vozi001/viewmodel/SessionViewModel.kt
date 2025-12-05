package com.example.vozi001.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vozi001.service.SpeechListener
import com.example.vozi001.service.SpeechRecognitionService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SessionUiState(
    val isListening: Boolean = false,
    val heardText: String = "",
    val scoreStars: Int = 0,
    val targetWord: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastPracticeSaved: Boolean = false,
    val speechAvailable: Boolean = true, // Nuevo estado
    val isInitialized: Boolean = false
)

class SessionViewModel(
    application: Application
) : AndroidViewModel(application), SpeechListener {

    companion object {
        private const val TAG = "VoziApp/ViewModel"
        
        // Lista de palabras para practicar pronunciación
        private val PALABRAS_PRACTICA = listOf(
            "PERRO",
            "GATO",
            "CASA",
            "MESA",
            "SILLA",
            "AGUA",
            "LIBRO",
            "PELOTA",
            "MANZANA",
            "NIÑO",
            "FLOR",
            "SOL",
            "LUNA",
            "ÁRBOL",
            "PÁJARO"
        )
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val speechService: SpeechRecognitionService

    // Selecciona una palabra aleatoria cada vez
    private val currentTargetWord = PALABRAS_PRACTICA.random()

    private val _uiState = MutableStateFlow(
        SessionUiState(
            targetWord = currentTargetWord,
            speechAvailable = true,
            isInitialized = false
        )
    )
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "=== INICIALIZANDO SessionViewModel ===")
        speechService = SpeechRecognitionService(application, this)
        _uiState.update { it.copy(isInitialized = true) }
        Log.d(TAG, "✅ ViewModel inicializado")
    }

    fun startPractice() {
        Log.d(TAG, "🔄 startPractice() llamado")

        if (_uiState.value.isListening) {
            Log.d(TAG, "⚠️ Ya está escuchando, ignorando startPractice")
            return
        }

        if (!_uiState.value.speechAvailable) {
            Log.e(TAG, "❌ No se puede iniciar - speechAvailable es false")
            _uiState.update {
                it.copy(
                    errorMessage = "El reconocimiento de voz no está disponible en este dispositivo. " +
                            "Por favor, instala Google Voice Typing desde Play Store."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                heardText = "",
                scoreStars = 0,
                errorMessage = null,
                lastPracticeSaved = false,
                isListening = true
            )
        }

        Log.d(TAG, "🎤 Llamando a speechService.startListening()")
        speechService.startListening()
    }

    fun stopPractice() {
        Log.d(TAG, "⏹️ stopPractice() llamado")
        speechService.stopListening()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun retrySpeechSetup() {
        Log.d(TAG, "🔄 Reintentando configuración de voz")
        // Podrías reinicializar el servicio aquí si es necesario
        _uiState.update { it.copy(errorMessage = null) }
    }

    // --- Implementación de SpeechListener ---

    override fun onSpeechResult(result: String) {
        Log.d(TAG, "📝 onSpeechResult: \"$result\"")

        _uiState.update { it.copy(heardText = result) }

        val stars = calculateStars(result, currentTargetWord)
        Log.d(TAG, "⭐ Estrellas calculadas: $stars")

        _uiState.update { it.copy(scoreStars = stars) }

        savePracticeResult(currentTargetWord, result, stars)
    }

    override fun onSpeechError(error: String) {
        Log.e(TAG, "❌ onSpeechError: $error")
        _uiState.update {
            it.copy(
                errorMessage = "Error de voz: $error",
                isListening = false
            )
        }
    }

    override fun onListeningStatusChange(isListening: Boolean) {
        Log.d(TAG, "🔄 onListeningStatusChange: $isListening")
        _uiState.update { it.copy(isListening = isListening) }
    }

    override fun onSpeechAvailable(available: Boolean) {
        Log.d(TAG, "📊 onSpeechAvailable: $available")
        _uiState.update { it.copy(speechAvailable = available) }

        if (!available) {
            Log.e(TAG, "⚠️ Speech recognition NO disponible")
            _uiState.update {
                it.copy(
                    errorMessage = "Reconocimiento de voz no disponible. " +
                            "Instala 'Google Voice Typing' desde Play Store o usa un dispositivo con Google Services."
                )
            }
        }
    }

    // --- Lógica de guardado ---

    private fun savePracticeResult(target: String, heard: String, stars: Int) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                Log.d(TAG, "💾 Guardando resultado localmente...")

                // Simular guardado en Room (implementar después)
                val timestamp = System.currentTimeMillis()
                Log.d(TAG, "✅ Guardado local simulado")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lastPracticeSaved = true
                    )
                }

                // Intentar subir a Firestore
                uploadToFirestore(target, heard, stars, timestamp)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error guardando resultado: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error guardando resultado: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun uploadToFirestore(target: String, heard: String, stars: Int, timestamp: Long) {
        try {
            Log.d(TAG, "☁️ Subiendo a Firestore...")

            val childId = "NINO-882" // Reemplazar con Auth.uid real

            val practiceData = hashMapOf(
                "childId" to childId,
                "targetWord" to target,
                "heardText" to heard,
                "stars" to stars,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            db.collection("practices").add(practiceData).await()
            Log.d(TAG, "✅ Subida a Firestore exitosa")

        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Falló subida a Firestore: ${e.message}")
            // No mostramos error al usuario porque el guardado local ya fue exitoso
        }
    }

    // --- Lógica de puntuación ---

    private fun calculateStars(heard: String, target: String): Int {
        val cleanTarget = target.uppercase().trim()
        val cleanHeard = heard.uppercase().trim()

        if (cleanHeard.isEmpty()) return 1

        var correctCount = 0
        val length = cleanTarget.length

        for (i in cleanTarget.indices) {
            if (i < cleanHeard.length && cleanTarget[i] == cleanHeard[i]) {
                correctCount++
            }
        }

        val accuracy = if (length > 0) (correctCount.toFloat() / length) * 100 else 0f
        Log.d(TAG, "📊 Precisión: $accuracy% ($correctCount/$length correctos)")

        return when {
            accuracy > 80 -> 3
            accuracy >= 50 -> 2
            else -> 1
        }
    }

    override fun onCleared() {
        Log.d(TAG, "🗑️ Limpiando ViewModel...")
        speechService.destroy()
        super.onCleared()
    }
}