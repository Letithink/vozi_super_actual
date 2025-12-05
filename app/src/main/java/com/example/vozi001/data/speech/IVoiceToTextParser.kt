package com.example.vozi001.data.speech

import android.os.Bundle

/**
 * Interfaz para parsear resultados de reconocimiento de voz
 */
interface IVoiceToTextParser {
    
    /**
     * Parsea los resultados finales del reconocimiento de voz
     */
    fun parseResults(results: Bundle?): String
    
    /**
     * Parsea resultados parciales del reconocimiento de voz
     */
    fun parsePartialResults(partialResults: Bundle?): String
    
    /**
     * Normaliza un texto eliminando acentos y caracteres especiales
     */
    fun normalizeText(text: String): String
    
    /**
     * Compara dos textos y retorna la similitud (0.0 a 1.0)
     */
    fun compareTexts(text1: String, text2: String): Float
}
