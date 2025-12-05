package com.example.vozi001.data.db

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para la entidad Practice
 */
class PracticeTest {
    
    @Test
    fun `Practice creation with default values`() {
        val practice = Practice(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5
        )
        
        assertEquals(0L, practice.id)
        assertEquals("user123", practice.userId)
        assertEquals("perro", practice.targetWord)
        assertEquals("perro", practice.heardWord)
        assertEquals(5, practice.stars)
        assertTrue(practice.timestamp > 0)
        assertEquals(0L, practice.duration)
        assertEquals(0f, practice.accuracy, 0.01f)
    }
    
    @Test
    fun `Practice creation with all values`() {
        val practice = Practice(
            id = 1,
            userId = "user123",
            targetWord = "gato",
            heardWord = "gato",
            stars = 4,
            timestamp = 123456789L,
            duration = 5000L,
            accuracy = 0.95f
        )
        
        assertEquals(1L, practice.id)
        assertEquals("user123", practice.userId)
        assertEquals("gato", practice.targetWord)
        assertEquals("gato", practice.heardWord)
        assertEquals(4, practice.stars)
        assertEquals(123456789L, practice.timestamp)
        assertEquals(5000L, practice.duration)
        assertEquals(0.95f, practice.accuracy, 0.01f)
    }
    
    @Test
    fun `Practice copy works correctly`() {
        val original = Practice(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 3
        )
        
        val copy = original.copy(stars = 5)
        
        assertEquals(5, copy.stars)
        assertEquals(original.userId, copy.userId)
        assertEquals(original.targetWord, copy.targetWord)
    }
    
    @Test
    fun `Practice equals works correctly`() {
        val practice1 = Practice(
            id = 1,
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5
        )
        
        val practice2 = Practice(
            id = 1,
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5
        )
        
        assertEquals(practice1, practice2)
    }
}
