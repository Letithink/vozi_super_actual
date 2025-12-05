package com.example.vozi001.data.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

/**
 * Gestor de sonidos para reproducir efectos de audio en la app
 */
class SoundManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SoundManager"
        private const val MAX_STREAMS = 5
    }
    
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundType, Int>()
    private var isInitialized = false
    
    enum class SoundType {
        SUCCESS,
        ERROR,
        CLICK,
        START,
        STOP
    }
    
    init {
        initializeSoundPool()
    }
    
    private fun initializeSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            soundPool = SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()
            
            isInitialized = true
            Log.d(TAG, "SoundPool initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing SoundPool: ${e.message}")
            isInitialized = false
        }
    }
    
    /**
     * Carga un sonido desde los recursos
     */
    fun loadSound(soundType: SoundType, resourceId: Int) {
        soundPool?.let { pool ->
            try {
                val soundId = pool.load(context, resourceId, 1)
                soundMap[soundType] = soundId
                Log.d(TAG, "Sound loaded: $soundType")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading sound $soundType: ${e.message}")
            }
        }
    }
    
    /**
     * Reproduce un sonido de éxito
     */
    fun playSuccess() {
        playSound(SoundType.SUCCESS)
    }
    
    /**
     * Reproduce un sonido de error
     */
    fun playError() {
        playSound(SoundType.ERROR)
    }
    
    /**
     * Reproduce un sonido de click
     */
    fun playClick() {
        playSound(SoundType.CLICK)
    }
    
    /**
     * Reproduce un sonido de inicio
     */
    fun playStart() {
        playSound(SoundType.START)
    }
    
    /**
     * Reproduce un sonido de parada
     */
    fun playStop() {
        playSound(SoundType.STOP)
    }
    
    /**
     * Reproduce un sonido específico
     */
    private fun playSound(soundType: SoundType, volume: Float = 1.0f) {
        if (!isInitialized) {
            Log.w(TAG, "SoundPool not initialized, cannot play sound")
            return
        }
        
        soundPool?.let { pool ->
            soundMap[soundType]?.let { soundId ->
                try {
                    pool.play(soundId, volume, volume, 1, 0, 1.0f)
                    Log.d(TAG, "Playing sound: $soundType")
                } catch (e: Exception) {
                    Log.e(TAG, "Error playing sound $soundType: ${e.message}")
                }
            } ?: Log.w(TAG, "Sound not loaded: $soundType")
        }
    }
    
    /**
     * Libera los recursos del SoundPool
     */
    fun release() {
        try {
            soundPool?.release()
            soundPool = null
            soundMap.clear()
            isInitialized = false
            Log.d(TAG, "SoundPool released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundPool: ${e.message}")
        }
    }
}
