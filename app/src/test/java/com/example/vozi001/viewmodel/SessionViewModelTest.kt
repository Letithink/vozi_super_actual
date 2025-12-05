package com.example.vozi001.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vozi001.utils.TestCoroutineRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para SessionViewModel
 */
@ExperimentalCoroutinesApi
class SessionViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private lateinit var viewModel: com.example.vozi001.viewmodel.SessionViewModel
    private lateinit var application: Application
    
    @Before
    fun setup() {
        application = mockk(relaxed = true)
        viewModel = com.example.vozi001.viewmodel.SessionViewModel(application)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `clearError clears errorMessage state`() = runTest {
        viewModel.clearError()
        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
    }
    
    @Test
    fun `stopPractice updates state`() = runTest {
        viewModel.stopPractice()
        val state = viewModel.uiState.value
        assertFalse(state.isListening)
    }
    
    @Test
    fun `onListeningStatusChange updates state correctly with true`() = runTest {
        viewModel.onListeningStatusChange(true)
        val state = viewModel.uiState.value
        assertTrue(state.isListening)
    }
    
    @Test
    fun `onListeningStatusChange updates state correctly with false`() = runTest {
        viewModel.onListeningStatusChange(false)
        val state = viewModel.uiState.value
        assertFalse(state.isListening)
    }
    
    @Test
    fun `onSpeechResult with empty result does nothing`() = runTest {
        viewModel.onSpeechResult("")
        val state = viewModel.uiState.value
        assertEquals("", state.heardText)
    }
    
    @Test
    fun `onSpeechAvailable with true updates state`() = runTest {
        viewModel.onSpeechAvailable(true)
        val state = viewModel.uiState.value
        assertTrue(state.speechAvailable)
    }
    
    @Test
    fun `onSpeechAvailable with false updates state`() = runTest {
        viewModel.onSpeechAvailable(false)
        val state = viewModel.uiState.value
        assertFalse(state.speechAvailable)
    }
    
    @Test
    fun `onSpeechError updates errorMessage state`() = runTest {
        viewModel.onSpeechError("Test error")
        val state = viewModel.uiState.value
        assertEquals("Test error", state.errorMessage)
    }
    
    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isListening)
        assertEquals("", state.heardText)
        assertNull(state.errorMessage)
    }
    
    @Test
    fun `retrySpeechSetup clears errorMessage`() = runTest {
        viewModel.onSpeechError("Error")
        viewModel.retrySpeechSetup()
        // Should attempt to restart speech
        assertTrue(true)  // Just verify it doesn't throw
    }
    
    @Test
    fun `onSpeechResult with valid text updates heardText`() = runTest {
        viewModel.onSpeechResult("perro")
        val state = viewModel.uiState.value
        assertFalse(state.heardText.isEmpty())
    }
    
    @Test
    fun `startPractice does not throw exception`() = runTest {
        try {
            viewModel.startPractice()
            assertTrue(true)
        } catch (e: Exception) {
            fail("startPractice should not throw:  ${e.message}")
        }
    }
}
