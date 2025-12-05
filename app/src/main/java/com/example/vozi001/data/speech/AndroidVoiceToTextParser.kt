package com.example.vozi001.data.speech

import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import java.text.Normalizer

/**
 * Parser para procesar resultados de reconocimiento de voz de Android
 */
class AndroidVoiceToTextParser : IVoiceToTextParser {
    
    companion object {
        private const val TAG = "VoiceToTextParser"
    }
    
    /**
     * Parsea los resultados del reconocimiento de voz
     */
    override fun parseResults(results: Bundle?): String {
        return try {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            
            if (matches.isNullOrEmpty()) {
                Log.w(TAG, "No speech recognition results")
                ""
            } else {
                val result = matches[0]
                val normalized = normalizeText(result)
                Log.d(TAG, "Parsed result: '$result' -> '$normalized'")
                normalized
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing results: ${e.message}")
            ""
        }
    }
    
    /**
     * Parsea resultados parciales del reconocimiento de voz
     */
    override fun parsePartialResults(partialResults: Bundle?): String {
        return try {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            
            if (matches.isNullOrEmpty()) {
                ""
            } else {
                normalizeText(matches[0])
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing partial results: ${e.message}")
            ""
        }
    }
    
    /**
     * Normaliza el texto eliminando acentos y convirtiendo a minúsculas
     */
    override fun normalizeText(text: String): String {
        return try {
            text.trim()
                .lowercase()
                .removeAccents()
                .replace(Regex("[^a-záéíóúñ\\s]"), "") // Mantener español
                .replace(Regex("\\s+"), " ")
                .trim()
        } catch (e: Exception) {
            Log.e(TAG, "Error normalizing text: ${e.message}")
            text.trim().lowercase()
        }
    }
    
    /**
     * Elimina acentos del texto
     */
    private fun String.removeAccents(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
    
    /**
     * Compara dos textos normalizados
     */
    override fun compareTexts(text1: String, text2: String): Float {
        val normalized1 = normalizeText(text1)
        val normalized2 = normalizeText(text2)
        
        return if (normalized1 == normalized2) {
            1.0f
        } else {
            calculateSimilarity(normalized1, normalized2)
        }
    }
    
    /**
     * Calcula la similitud entre dos textos usando la distancia de Levenshtein
     */
    private fun calculateSimilarity(s1: String, s2: String): Float {
        if (s1.isEmpty() && s2.isEmpty()) return 1.0f
        if (s1.isEmpty() || s2.isEmpty()) return 0.0f
        
        val distance = levenshteinDistance(s1, s2)
        val maxLength = maxOf(s1.length, s2.length)
        
        return 1.0f - (distance.toFloat() / maxLength.toFloat())
    }
    
    /**
     * Calcula la distancia de Levenshtein entre dos strings
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }
        
        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j
        
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return dp[len1][len2]
    }
}
