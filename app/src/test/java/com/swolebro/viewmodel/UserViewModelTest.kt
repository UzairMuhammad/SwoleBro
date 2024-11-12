package com.swolebro.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.swolebro.model.UserRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.Rule

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var testViewModel: UserViewModel
    private lateinit var testRepository: UserRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        testRepository = mockk(relaxed = true)
        testViewModel = UserViewModel(testRepository)
    }

    @Test
    fun updateName() {
        val updatedName = "Updated Name"
        testViewModel.updateName(updatedName)
        assertEquals(updatedName, testViewModel.name.value)
    }

    @Test
    fun updateUnits() {
        val newUnits = "lbs"
        testViewModel.updateUnits(newUnits)
        assertEquals(newUnits, testViewModel.defaultUnits.value)
    }

    @Test
    fun isCalculatingBmi() {
        testViewModel.calculateBMI()
        assertTrue("Expected isCalculatingBmi to be true during BMI calculation", testViewModel.isCalculatingBmi.value)
    }

    @Test
    fun getDefaultValues() {
        val defaultUnits = testViewModel.defaultUnits.value
        assertEquals("kg", defaultUnits)
    }

}