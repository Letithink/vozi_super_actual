package com.example.vozi001.data.sound

import android.content.Context
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para SoundManager
 */
class SoundManagerTest {
    
    private lateinit var context: Context
    private lateinit var soundManager: SoundManager
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        soundManager = SoundManager(context)
    }
    
    @After
    fun tearDown() {
        soundManager.release()
        clearAllMocks()
    }
    
    @Test
    fun `playSuccess does not throw exception`() {
        try {
            soundManager.playSuccess()
            assertTrue(true)
        } catch (e: Exception) {
            fail("playSuccess should not throw exception")
        }
    }
    
    @Test
    fun `playError does not throw exception`() {
        try {
            soundManager.playError()
            assertTrue(true)
        } catch (e: Exception) {
            fail("playError should not throw exception")
        }
    }
    
    @Test
    fun `playClick does not throw exception`() {
        try {
            soundManager.playClick()
            assertTrue(true)
        } catch (e: Exception) {
            fail("playClick should not throw exception")
        }
    }
    
    @Test
    fun `release does not throw exception`() {
        try {
            soundManager.release()
            assertTrue(true)
        } catch (e: Exception) {
            fail("release should not throw exception")
        }
    }
    
    @Test
    fun `multiple releases are safe`() {
        soundManager.release()
        soundManager.release()
        assertTrue(true)  // No crash
    }
}
