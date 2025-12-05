package com.example.vozi001.data.speech

import android.os.Bundle
import android.speech.SpeechRecognizer
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para AndroidVoiceToTextParser
 */
class AndroidVoiceToTextParserTest {
    
    private lateinit var parser: AndroidVoiceToTextParser
    
    @Before
    fun setup() {
        parser = AndroidVoiceToTextParser()
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `normalizeText removes accents`() {
        val result = parser.normalizeText("café")
        assertEquals("cafe", result)
    }
    
    @Test
    fun `normalizeText converts to lowercase`() {
        val result = parser.normalizeText("PERRO")
        assertEquals("perro", result)
    }
    
    @Test
    fun `normalizeText trims spaces`() {
        val result = parser.normalizeText("  perro  ")
        assertEquals("perro", result)
    }
    
    @Test
    fun `normalizeText handles multiple spaces`() {
        val result = parser.normalizeText("perro    grande")
        assertEquals("perro grande", result)
    }
    
    @Test
    fun `normalizeText handles empty string`() {
        val result = parser.normalizeText("")
        assertEquals("", result)
    }
    
    @Test
    fun `normalizeText handles spanish characters`() {
        val result = parser.normalizeText("niño")
        assertEquals("nino", result)
    }
    
    @Test
    fun `compareTexts returns 1 for identical texts`() {
        val result = parser.compareTexts("perro", "perro")
        assertEquals(1.0f, result, 0.01f)
    }
    
    @Test
    fun `compareTexts returns 0 for completely different texts`() {
        val result = parser.compareTexts("aaaaa", "bbbbb")
        assertTrue(result < 0.3f)
    }
    
    @Test
    fun `compareTexts ignores case`() {
        val result = parser.compareTexts("PERRO", "perro")
        assertEquals(1.0f, result, 0.01f)
    }
    
    @Test
    fun `compareTexts handles similar texts`() {
        val result = parser.compareTexts("perro", "pero")
        assertTrue(result > 0.6f)
    }
    
    @Test
    fun `parseResults with null bundle returns empty string`() {
        val result = parser.parseResults(null)
        assertEquals("", result)
    }
    
    @Test
    fun `parseResults with empty results returns empty string`() {
        val bundle = mockk<Bundle>()
        every { bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) } returns ArrayList()
        
        val result = parser.parseResults(bundle)
        assertEquals("", result)
    }
    
    @Test
    fun `parsePartialResults with null returns empty string`() {
        val result = parser.parsePartialResults(null)
        assertEquals("", result)
    }
}
