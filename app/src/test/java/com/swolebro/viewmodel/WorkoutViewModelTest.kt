package com.swolebro.viewmodel

import org.junit.Before
import org.junit.Assert.*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.swolebro.model.Exercise
import com.swolebro.model.UserRepository
import com.swolebro.model.Workout
import com.swolebro.model.WorkoutController
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class WorkoutViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var testViewModel: WorkoutViewModel
    private lateinit var testRepository: UserRepository

    @Before
    fun setUp() {
        testRepository = mockk(relaxed = true)
        testViewModel = WorkoutViewModel(testRepository)
    }

    @Test
    fun logTemplateTest() = runBlockingTest {
        val name = "Logged Workout"
        val note = "Sample note"
        val loggedExercises = mutableListOf<Exercise>()
        val workoutController = mockk<WorkoutController>(relaxed = true)
        testViewModel.workoutController = workoutController
        testViewModel.logTemplate(name, loggedExercises, note)
        coVerify { workoutController.endWorkout(any()) }
    }

    @Test
    fun workoutInProgressTest() {
        testViewModel.workoutController = null
        val inProgress = testViewModel.workoutInProgress()
        assertFalse(inProgress) //not in progress on initialization
    }

    @Test
    fun getActiveWorkout() {
        testViewModel.workoutController = null
        val activeWorkout = testViewModel.getActiveWorkout()
        assertNull(activeWorkout) //null on initialization
    }

    @Test
    fun setActiveWorkout() {
        val expectedWorkout = mockk<Workout>(relaxed = true)
        testViewModel.workoutController = WorkoutController(expectedWorkout, mockk(relaxed = true))
        testViewModel.workoutController!!.activeWorkout = expectedWorkout

        val activeWorkout = testViewModel.getActiveWorkout()
        assertEquals(expectedWorkout, activeWorkout)
    }

}