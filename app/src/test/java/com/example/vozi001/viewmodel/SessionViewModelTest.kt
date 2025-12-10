package com.example.vozi001.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vozi001.service.SpeechRecognitionService
import com.example.vozi001.utils.TestCoroutineRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SessionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: SessionViewModel
    private lateinit var application: Application
    private lateinit var mockSpeechService: SpeechRecognitionService

    @Before
    fun setup() {
        // 1. Preparamos los Mocks simples
        application = mockk(relaxed = true)

        // Creamos un mock del servicio DIRECTAMENTE.
        // Ya no necesitamos mockkConstructor ni estáticos complejos.
        mockSpeechService = mockk(relaxed = true)

        // 2. Inyectamos el mock en el ViewModel
        viewModel = SessionViewModel(application, mockSpeechService)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // --- TUS PRUEBAS AHORA PASARÁN ---

    @Test
    fun `startPractice calls service startListening`() = runTest {
        // Ejecutar
        viewModel.startPractice()

        // Verificar que el ViewModel llamó al método del servicio
        verify { mockSpeechService.startListening() }
    }

    @Test
    fun `stopPractice calls service stopListening`() = runTest {
        viewModel.stopPractice()
        verify { mockSpeechService.stopListening() }

        // Verificamos el estado
        val state = viewModel.uiState.value
        assertFalse(state.isListening)
    }

    @Test
    fun `onListeningStatusChange updates state correctly`() = runTest {
        viewModel.onListeningStatusChange(true)
        assertTrue(viewModel.uiState.value.isListening)

        viewModel.onListeningStatusChange(false)
        assertFalse(viewModel.uiState.value.isListening)
    }

    @Test
    fun `onSpeechResult updates heardText`() = runTest {
        val testText = "Hola mundo"
        viewModel.onSpeechResult(testText)
        assertEquals(testText, viewModel.uiState.value.heardText)
    }

    @Test
    fun `onSpeechError updates errorMessage`() = runTest {
        val errorMsg = "Error de conexión"
        viewModel.onSpeechError(errorMsg)
        assertEquals(errorMsg, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isListening)
        assertTrue(state.heardText.isEmpty())
        assertNull(state.errorMessage)
    }
}