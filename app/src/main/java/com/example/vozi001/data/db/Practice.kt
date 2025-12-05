package com.example.vozi001.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room Database que representa una práctica de pronunciación
 */
@Entity(tableName = "practices")
data class Practice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val userId: String,
    
    val targetWord: String,
    
    val heardWord: String,
    
    val stars: Int,
    
    val timestamp: Long = System.currentTimeMillis(),
    
    val duration: Long = 0L, // Duración en milisegundos
    
    val accuracy: Float = 0f // Precisión 0.0 a 1.0
)
