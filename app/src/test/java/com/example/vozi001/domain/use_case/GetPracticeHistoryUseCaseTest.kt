package com.example.vozi001.domain.use_case

import com.example.vozi001.data.db.Practice
import com.example.vozi001.domain.repository.PracticeRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para GetPracticeHistoryUseCase
 */
@ExperimentalCoroutinesApi
class GetPracticeHistoryUseCaseTest {
    
    private lateinit var repository: PracticeRepository
    private lateinit var useCase: GetPracticeHistoryUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPracticeHistoryUseCase(repository)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `invoke with valid userId returns flow`() = runTest {
        val practices = listOf(
            Practice(1, "user123", "perro", "perro", 5),
            Practice(2, "user123", "gato", "gato", 4)
        )
        every { repository.getPracticesByUser("user123") } returns flowOf(practices)
        
        val result = useCase("user123")
        assertNotNull(result)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `invoke with empty userId throws exception`() {
        useCase("")
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `invoke with blank userId throws exception`() {
        useCase("   ")
    }
    
    @Test
    fun `getRecent with valid params returns success`() = runTest {
        val practices = listOf(
            Practice(1, "user123", "perro", "perro", 5)
        )
        coEvery { repository.getRecentPractices("user123", 10) } returns practices
        
        val result = useCase.getRecent("user123", 10)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
    
    @Test
    fun `getRecent with empty userId returns failure`() = runTest {
        val result = useCase.getRecent("", 10)
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getRecent with zero limit returns failure`() = runTest {
        val result = useCase.getRecent("user123", 0)
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getRecent with negative limit returns failure`() = runTest {
        val result = useCase.getRecent("user123", -1)
        assertTrue(result.isFailure)
    }
}
