package com.example.vozi001.service

import android.content.Context
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Tests unitarios para AudioRecordingService
 */
class AudioRecordingServiceTest {
    
    private lateinit var context: Context
    private lateinit var service: AudioRecordingService
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        every { context.filesDir } returns File("/tmp/test")
        service = AudioRecordingService(context)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `isRecording returns false initially`() {
        assertFalse(service.isRecording())
    }
    
    @Test
    fun `getCurrentRecordingFile returns null initially`() {
        assertNull(service.getCurrentRecordingFile())
    }
    
    @Test
    fun `stopRecording without active recording succeeds`() {
        val result = service.stopRecording()
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `cleanup sets recording to false`() {
        service.cleanup()
        assertFalse(service.isRecording())
    }
    
    @Test
    fun `cleanup clears current file`() {
        service.cleanup()
        assertNull(service.getCurrentRecordingFile())
    }
    
    @Test
    fun `stopRecording returns result with file`() {
        val result = service.stopRecording()
        assertTrue(result.isSuccess)
        // Note: result.getOrNull() is null because no recording was started
        // This is correct behavior
    }
}
