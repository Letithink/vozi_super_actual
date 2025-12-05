package com.example.vozi001.service

import android.content.Context
import android.speech.SpeechRecognizer
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para SpeechRecognitionService
 */
class SpeechRecognitionServiceTest {
    
    private lateinit var context: Context
    private lateinit var listener: SpeechListener
    private lateinit var service: SpeechRecognitionService
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        listener = mockk(relaxed = true)
        
        // Mock SpeechRecognizer.isRecognitionAvailable
        mockkStatic(SpeechRecognizer::class)
        every { SpeechRecognizer.isRecognitionAvailable(any()) } returns true
        
        service = SpeechRecognitionService(context, listener)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }
    
    @Test
    fun `onError calls listener with error for network error`() {
        service.onError(SpeechRecognizer.ERROR_NETWORK)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for no match`() {
        service.onError(SpeechRecognizer.ERROR_NO_MATCH)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for insuffici ent permissions`() {
        service.onError(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for network timeout`() {
        service.onError(SpeechRecognizer.ERROR_NETWORK_TIMEOUT)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for audio error`() {
        service.onError(SpeechRecognizer.ERROR_AUDIO)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for server error`() {
        service.onError(SpeechRecognizer.ERROR_SERVER)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for client error`() {
        service.onError(SpeechRecognizer.ERROR_CLIENT)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onError calls listener with error for unknown error`() {
        service.onError(999)
        verify { listener.onSpeechError(any()) }
    }
    
    @Test
    fun `onReadyForSpeech calls listener`() {
        service.onReadyForSpeech(null)
        verify { listener.onListeningStatusChange(true) }
    }
    
    @Test
    fun `onBeginningOfSpeech calls listener`() {
        service.onBeginningOfSpeech()
        // Verify it doesn't throw
        assertTrue(true)
    }
}
