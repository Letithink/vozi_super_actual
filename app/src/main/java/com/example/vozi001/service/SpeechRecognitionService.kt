package com.example.vozi001.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

interface SpeechListener {
    fun onSpeechResult(result: String)
    fun onSpeechError(error: String)
    fun onListeningStatusChange(isListening: Boolean)
    fun onSpeechAvailable(available: Boolean) // Nuevo callback
}

class SpeechRecognitionService(
    private val context: Context,
    private val listener: SpeechListener
) : RecognitionListener {

    companion object {
        private const val TAG = "VoziApp/SpeechService"
    }

    private val speechRecognizer: SpeechRecognizer?
    private val recognizerIntent: Intent

    init {
        Log.d(TAG, "=== INICIALIZANDO SpeechRecognitionService ===")
        Log.d(TAG, "Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}")
        Log.d(TAG, "Android API: ${Build.VERSION.SDK_INT}")
        Log.d(TAG, "Paquete: ${context.packageName}")

        // Verificar disponibilidad inmediatamente
        val isAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        Log.d(TAG, "SpeechRecognizer.isRecognitionAvailable(): $isAvailable")

        // Notificar al listener sobre disponibilidad
        listener.onSpeechAvailable(isAvailable)

        if (!isAvailable) {
            Log.e(TAG, "❌ ERROR: Google Speech Recognition NO está disponible")
            Log.e(TAG, "Posibles causas:")
            Log.e(TAG, "1. Dispositivo sin Google Play Services")
            Log.e(TAG, "2. Emulador sin Google APIs")
            Log.e(TAG, "3. Aplicación de voz desactivada")
            speechRecognizer = null
        } else {
            speechRecognizer = try {
                SpeechRecognizer.createSpeechRecognizer(context).apply {
                    Log.d(TAG, "SpeechRecognizer creado exitosamente")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al crear SpeechRecognizer: ${e.message}", e)
                null
            }
        }

        // Inicializar intent
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)

            // Para MIUI/Xiaomi - intentar mejorar compatibilidad
            if (Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)) {
                Log.d(TAG, "Dispositivo Xiaomi detectado, ajustando configuración")
                putExtra("android.speech.extra.DICTATION_MODE", true)
            }

            Log.d(TAG, "Intent configurado con idioma: ${Locale.getDefault()}")
        }

        speechRecognizer?.setRecognitionListener(this)
        Log.d(TAG, "=== INICIALIZACIÓN COMPLETADA ===")
    }

    fun startListening() {
        Log.d(TAG, "🎤 === startListening() INVOCADO ===")

        if (speechRecognizer == null) {
            Log.e(TAG, "❌ speechRecognizer es NULL - no se puede iniciar")
            listener.onSpeechError("Servicio de voz no disponible en este dispositivo")
            return
        }

        val isAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        Log.d(TAG, "📊 Disponibilidad verificada: $isAvailable")

        if (!isAvailable) {
            Log.e(TAG, "❌ Servicio no disponible al intentar iniciar")
            listener.onSpeechError("El reconocimiento de voz no está disponible. Instala Google Voice Typing.")
            return
        }

        try {
            Log.d(TAG, "🔄 Iniciando reconocimiento...")
            speechRecognizer.startListening(recognizerIntent)
            listener.onListeningStatusChange(true)
            Log.d(TAG, "✅ Reconocimiento iniciado exitosamente")
        } catch (e: SecurityException) {
            Log.e(TAG, "🔒 SecurityException: ${e.message}", e)
            listener.onSpeechError("Permiso de micrófono denegado. Verifica los permisos de audio.")
            listener.onListeningStatusChange(false)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "⚠️ IllegalArgumentException: ${e.message}", e)
            listener.onSpeechError("Configuración de voz inválida: ${e.message}")
            listener.onListeningStatusChange(false)
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error general: ${e.message}", e)
            listener.onSpeechError("Error al iniciar: ${e.message}")
            listener.onListeningStatusChange(false)
        }
    }

    fun stopListening() {
        Log.d(TAG, "⏹️ Deteniendo reconocimiento...")
        speechRecognizer?.stopListening()
        listener.onListeningStatusChange(false)
    }

    fun destroy() {
        Log.d(TAG, "🗑️ Destruyendo SpeechRecognizer...")
        speechRecognizer?.destroy()
    }

    // --- Callbacks del RecognitionListener ---

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "✅ Listo para escuchar (onReadyForSpeech)")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "🎙️ Comienzo del habla detectado")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Opcional: para visualización de nivel de audio
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "📦 Buffer recibido")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "⏹️ Fin del habla detectado")
    }

    override fun onError(error: Int) {
        val message = getErrorText(error)
        Log.e(TAG, "❌ Error de reconocimiento: $message (código: $error)")
        listener.onSpeechError(message)
        listener.onListeningStatusChange(false)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val result = matches?.firstOrNull() ?: ""
        Log.d(TAG, "📝 Resultado recibido: \"$result\"")
        Log.d(TAG, "🔢 Total de coincidencias: ${matches?.size ?: 0}")

        listener.onSpeechResult(result)
        listener.onListeningStatusChange(false)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partial = matches?.firstOrNull() ?: ""
        if (partial.isNotEmpty()) {
            Log.d(TAG, "📝 Resultado parcial: \"$partial\"")
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "🎭 Evento recibido: $eventType")
    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
            SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
            SpeechRecognizer.ERROR_NETWORK -> "Error de red"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de red agotado"
            SpeechRecognizer.ERROR_NO_MATCH -> "No se reconoció el habla"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
            SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de habla agotado"
            SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "Idioma no soportado"
            SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "Idioma no disponible"
            SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> "Servidor desconectado"
            SpeechRecognizer.ERROR_TOO_MANY_REQUESTS -> "Demasiadas solicitudes"
            else -> "Error desconocido ($errorCode)"
        }
    }
}