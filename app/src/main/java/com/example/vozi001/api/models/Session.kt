package com.example.vozi001.api.models

import com.google.firebase.Timestamp

/**
 * Modelo de datos para sesiones de terapia
 */
data class Session(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val words: List<String> = emptyList(),
    val therapistId: String = "",
    val childId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,
    val difficulty: SessionDifficulty = SessionDifficulty.MEDIUM,
    val category: String = ""
) {
    /**
     * Convierte a mapa para Firebase
     */
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "title" to title,
            "description" to description,
            "words" to words,
            "therapistId" to therapistId,
            "childId" to childId,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "isCompleted" to isCompleted,
            "difficulty" to difficulty.name,
            "category" to category
        )
        
        if (id.isNotEmpty()) {
            map["id"] = id
        }
        
        completedAt?.let {
            map["completedAt"] = it
        }
        
        return map
    }
    
    companion object {
        /**
         * Crea una sesión desde un mapa de Firebase
         */
        fun fromMap(id: String, data: Map<String, Any>): Session {
            return Session(
                id = id,
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                words = (data["words"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                therapistId = data["therapistId"] as? String ?: "",
                childId = data["childId"] as? String ?: "",
                createdAt = data["createdAt"] as? Timestamp ?: Timestamp.now(),
                updatedAt = data["updatedAt"] as? Timestamp ?: Timestamp.now(),
                isCompleted = data["isCompleted"] as? Boolean ?: false,
                completedAt = data["completedAt"] as? Timestamp,
                difficulty = SessionDifficulty.valueOf(
                    data["difficulty"] as? String ?: SessionDifficulty.MEDIUM.name
                ),
                category = data["category"] as? String ?: ""
            )
        }
    }
}

/**
 * Niveles de dificultad de las sesiones
 */
enum class SessionDifficulty {
    EASY,
    MEDIUM,
    HARD
}
