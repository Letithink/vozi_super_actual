package com.example.vozi001.domain.use_case

import com.example.vozi001.data.db.Practice
import com.example.vozi001.domain.repository.PracticeRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para UpdatePracticeUseCase
 */
@ExperimentalCoroutinesApi
class UpdatePracticeUseCaseTest {
    
    private lateinit var repository: PracticeRepository
    private lateinit var useCase: UpdatePracticeUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = UpdatePracticeUseCase(repository)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `invoke with valid practice returns success`() = runTest {
        val practice = Practice(1, "user123", "perro", "perro", 5)
        coEvery { repository.getPracticeById(1) } returns practice
        coEvery { repository.updatePractice(any()) } just Runs
        
        val result = useCase(practice)
        
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `invoke with invalid id returns failure`() = runTest {
        val practice = Practice(0, "user123", "perro", "perro", 5)
        
        val result = useCase(practice)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with empty userId returns failure`() = runTest {
        val practice = Practice(1, "", "perro", "perro", 5)
        
        val result = useCase(practice)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with non-existent practice returns failure`() = runTest {
        val practice = Practice(999, "user123", "perro", "perro", 5)
        coEvery { repository.getPracticeById(999) } returns null
        
        val result = useCase(practice)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `updateScore with valid params returns success`() = runTest {
        val practice = Practice(1, "user123", "perro", "perro", 3)
        coEvery { repository.getPracticeById(1) } returns practice
        coEvery { repository.updatePractice(any()) } just Runs
        
        val result = useCase.updateScore(1, 5, 0.95f)
        
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `updateScore with invalid stars returns failure`() = runTest {
        val result = useCase.updateScore(1, 0, 0.5f)
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `updateScore with invalid accuracy returns failure`() = runTest {
        val result = useCase.updateScore(1, 5, 1.5f)
        assertTrue(result.isFailure)
    }
}
