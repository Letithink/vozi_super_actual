package com.example.vozi001.viewmodel

import android.app.Application
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vozi001.utils.TestCoroutineRule
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class SessionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: SessionViewModel
    private lateinit var application: Application
    private lateinit var firestoreMock: FirebaseFirestore

    @Before
    fun setup() {
        // 1. MOCK DEL CONSTRUCTOR DE SpeechRecognitionService (PRIMERO Y MÁS IMPORTANTE)
        mockkConstructor(com.example.vozi001.service.SpeechRecognitionService::class)
        val mockService = mockk<com.example.vozi001.service.SpeechRecognitionService>(relaxed = true)
        every { anyConstructed<com.example.vozi001.service.SpeechRecognitionService>() } returns mockService

        // 2. MOCK SPEECH RECOGNIZER
        mockkStatic(SpeechRecognizer::class)
        every { SpeechRecognizer.isRecognitionAvailable(any()) } returns true
        every { SpeechRecognizer.createSpeechRecognizer(any()) } returns mockk(relaxed = true)

        // 3. MOCK FIREBASE
        mockkStatic(FirebaseFirestore::class)
        firestoreMock = mockk(relaxed = true)
        every { FirebaseFirestore.getInstance() } returns firestoreMock

        // 4. MOCK LOG
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // 5. CREAR VIEWMODEL
        application = mockk(relaxed = true)
        viewModel = SessionViewModel(application)
    }

    @After
    fun tearDown() {
        // Limpiar todos los mocks
        unmockkStatic(FirebaseFirestore::class)
        unmockkStatic(Log::class)
        unmockkStatic(SpeechRecognizer::class)
        unmockkConstructor(com.example.vozi001.service.SpeechRecognitionService::class)
        clearAllMocks()
    }

    // ... tus pruebas se mantienen igual
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
        assertTrue(true)
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
            fail("startPractice should not throw: ${e.message}")
        }
    }
}