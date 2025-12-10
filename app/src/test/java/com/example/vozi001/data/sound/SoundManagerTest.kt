package com.example.vozi001.data.sound

import android.content.Context
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.runner.RunWith // Import the RunWith annotation
import org.robolectric.RobolectricTestRunner // Import the Robolectric runner
import org.robolectric.annotation.Config

/**
 * Tests unitarios para SoundManager
 */
@RunWith(RobolectricTestRunner::class) // <-- ADD THIS LINE
@Config(manifest=Config.NONE)
class SoundManagerTest {

    // Note: The Log.e error happens when SoundManager is initialized,
    // likely within its initializeSoundPool or constructor.
    // Robolectric will fix this by providing a shadow implementation for Log.

    private lateinit var context: Context
    private lateinit var soundManager: SoundManager

    @Before
    fun setup() {
        // Since this is a Robolectric test, 'context' will be a real
        // ShadowContext, but mockk(relaxed = true) is still fine.
        context = mockk(relaxed = true)

        // This line caused the crash before, but should now work with Robolectric
        soundManager = SoundManager(context)
    }

    @After
    fun tearDown() {
        soundManager.release()
        clearAllMocks()
    }

    @Test
    fun `playSuccess does not throw exception`() {
        try {
            soundManager.playSuccess()
            assertTrue(true)
        } catch (e: Exception) {
            fail("playSuccess should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `playError does not throw exception`() {
        try {
            soundManager.playError()
            assertTrue(true)
        } catch (e: Exception) {
            fail("playError should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `playClick does not throw exception`() {
        try {
            soundManager.playClick()
            assertTrue(true)
        } catch (e: Exception) {
            fail("playClick should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `release does not throw exception`() {
        try {
            soundManager.release()
            assertTrue(true)
        } catch (e: Exception) {
            fail("release should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `multiple releases are safe`() {
        soundManager.release()
        soundManager.release()
        assertTrue(true)  // No crash
    }
}
