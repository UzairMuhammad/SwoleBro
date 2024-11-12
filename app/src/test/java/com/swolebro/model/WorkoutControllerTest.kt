package com.swolebro.model

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before
import java.util.*

class WorkoutControllerTest {

    private lateinit var testController: WorkoutController
    private lateinit var mockWorkout: Workout
    private lateinit var mockRepository: UserRepository
    private lateinit var mockTimer: Timer

    @Before
    fun setup() = runBlocking {
        mockWorkout = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        mockTimer = mockk(relaxed = true)

        every { mockRepository.getAuth().currentUser } returns mockk(relaxed = true)
        coEvery { mockRepository.getIncompleteWorkout() } returns null

        testController = WorkoutController(mockWorkout, mockRepository)
    }

    @Test
    fun startWorkout() = runBlocking {
        testController.startWorkout()
        assertTrue(testController.workoutStarted)
        assertFalse(testController.workoutPaused)
        coVerify { mockRepository.setActiveWorkout(any()) }
    }

    @Test
    fun pauseWorkout() {
        testController.startWorkout()
        testController.pauseWorkout()
        assertTrue(testController.workoutPaused)
    }

    @Test
    fun resumeWorkout() {
        testController.startWorkout()
        testController.pauseWorkout()
        testController.resumeWorkout()
        assertFalse(testController.workoutPaused)
    }

    @Test
    fun endWorkout() = runBlocking {
        testController.startWorkout()
        testController.endWorkout(mockWorkout)
        coVerify { mockRepository.setActiveWorkout(null) }
        coVerify { mockRepository.postNewWorkout(any()) }
    }

    @Test
    fun getWorkoutTime() {
        testController.startWorkout()
        Thread.sleep(1500)
        testController.pauseWorkout()
        val time = testController.getWorkoutTime()
        assertTrue(time >= 1000)
    }
}