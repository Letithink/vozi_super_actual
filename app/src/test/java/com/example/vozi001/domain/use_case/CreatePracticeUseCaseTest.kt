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
 * Tests unitarios para CreatePracticeUseCase
 */
@ExperimentalCoroutinesApi
class CreatePracticeUseCaseTest {
    
    private lateinit var repository: PracticeRepository
    private lateinit var useCase: CreatePracticeUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = CreatePracticeUseCase(repository)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `invoke with valid data returns success`() = runTest {
        coEvery { repository.insertPractice(any()) } returns 1L
        
        val result = useCase(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5
        )
        
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
    }
    
    @Test
    fun `invoke with empty userId returns failure`() = runTest {
        val result = useCase(
            userId = "",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5
        )
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with empty targetWord returns failure`() = runTest {
        val result = useCase(
            userId = "user123",
            targetWord = "",
            heardWord = "perro",
            stars = 5
        )
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with invalid stars returns failure`() = runTest {
        val result = useCase(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 0
        )
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with stars greater than 5 returns failure`() = runTest {
        val result = useCase(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 6
        )
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with invalid accuracy returns failure`() = runTest {
        val result = useCase(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5,
            accuracy = 1.5f
        )
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke with negative accuracy returns failure`() = runTest {
        val result = useCase(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5,
            accuracy = -0.1f
        )
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `invoke calls repository with correct practice`() = runTest {
        coEvery { repository.insertPractice(any()) } returns 1L
        
        useCase(
            userId = "user123",
            targetWord = "perro",
            heardWord = "perro",
            stars = 5,
            duration = 1000L,
            accuracy = 0.95f
        )
        
        coVerify {
            repository.insertPractice(
                match {
                    it.userId == "user123" &&
                    it.targetWord == "perro" &&
                    it.heardWord == "perro" &&
                    it.stars == 5 &&
                    it.duration == 1000L &&
                    it.accuracy == 0.95f
                }
            )
        }
    }
}
