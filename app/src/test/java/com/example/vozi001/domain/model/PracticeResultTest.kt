package com.example.vozi001.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para PracticeResult
 */
class PracticeResultTest {
    
    @Test
    fun `PracticeResult creation with all fields`() {
        val richText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Green)) {
                append("test")
            }
        }
        
        val result = PracticeResult(
            richTextResult = richText,
            accuracy = 0.95f,
            stars = 5,
            playSuccessSound = true,
            firstFailedChar = null
        )
        
        assertEquals(0.95f, result.accuracy, 0.01f)
        assertEquals(5, result.stars)
        assertTrue(result.playSuccessSound)
        assertNull(result.firstFailedChar)
    }
    
    @Test
    fun `PracticeResult with failed character`() {
        val richText = AnnotatedString("test")
        
        val result = PracticeResult(
            richTextResult = richText,
            accuracy = 0.5f,
            stars = 2,
            playSuccessSound = false,
            firstFailedChar = 'r'
        )
        
        assertEquals('r', result.firstFailedChar)
        assertFalse(result.playSuccessSound)
        assertEquals(2, result.stars)
    }
    
    @Test
    fun `PracticeResult copy works correctly`() {
        val richText = AnnotatedString("test")
        val original = PracticeResult(
            richTextResult = richText,
            accuracy = 0.8f,
            stars = 4,
            playSuccessSound = true,
            firstFailedChar = null
        )
        
        val copy = original.copy(stars = 3)
        
        assertEquals(3, copy.stars)
        assertEquals(original.accuracy, copy.accuracy, 0.01f)
    }
}
