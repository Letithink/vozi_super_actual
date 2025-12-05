package com.example.vozi001.api.models

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para el modelo Session
 */
class SessionTest {
    
    @Test
    fun `Session creation with defaults`() {
        val session = Session(
            title = "Sesión 1",
            description = "Test",
            therapistId = "therapist1",
            childId = "child1"
        )
        
        assertEquals("Sesión 1", session.title)
        assertEquals("Test", session.description)
        assertEquals("therapist1", session.therapistId)
        assertEquals("child1", session.childId)
        assertFalse(session.isCompleted)
        assertEquals(SessionDifficulty.MEDIUM, session.difficulty)
    }
    
    @Test
    fun `Session toMap includes all fields`() {
        val session = Session(
            id = "session1",
            title = "Sesión 1",
            description = "Test",
            therapistId = "therapist1",
            childId = "child1",
            words = listOf("perro", "gato"),
            difficulty = SessionDifficulty.HARD
        )
        
        val map = session.toMap()
        
        assertEquals("Sesión 1", map["title"])
        assertEquals("Test", map["description"])
        assertEquals("therapist1", map["therapistId"])
        assertEquals("child1", map["childId"])
        assertEquals("HARD", map["difficulty"])
        assertTrue(map["words"] is List<*>)
    }
    
    @Test
    fun `Session fromMap creates correct session`() {
        val map = mapOf(
            "title" to "Sesión 1",
            "description" to "Test",
            "therapistId" to "therapist1",
            "childId" to "child1",
            "words" to listOf("perro", "gato"),
            "isCompleted" to false,
            "difficulty" to "EASY",
            "category" to "animals",
            "createdAt" to Timestamp.now(),
            "updatedAt" to Timestamp.now()
        )
        
        val session = Session.fromMap("session1", map)
        
        assertEquals("session1", session.id)
        assertEquals("Sesión 1", session.title)
        assertEquals(SessionDifficulty.EASY, session.difficulty)
    }
    
    @Test
    fun `Session copy works correctly`() {
        val session = Session(
            title = "Original",
            therapistId = "t1",
            childId = "c1"
        )
        
        val copy = session.copy(title = "Modified")
        
        assertEquals("Modified", copy.title)
        assertEquals(session.therapistId, copy.therapistId)
    }
    
    @Test
    fun `Session with completed status has completedAt`() {
        val timestamp = Timestamp.now()
        val session = Session(
            title = "Test",
            therapistId = "t1",
            childId = "c1",
            isCompleted = true,
            completedAt = timestamp
        )
        
        assertTrue(session.isCompleted)
        assertEquals(timestamp, session.completedAt)
    }
    
    @Test
    fun `SessionDifficulty enum has all values`() {
        val values = SessionDifficulty.values()
        assertEquals(3, values.size)
        assertTrue(values.contains(SessionDifficulty.EASY))
        assertTrue(values.contains(SessionDifficulty.MEDIUM))
        assertTrue(values.contains(SessionDifficulty.HARD))
    }
}
