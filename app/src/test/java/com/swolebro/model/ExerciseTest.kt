package com.swolebro.model

import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class ExerciseTest {

    private lateinit var testExercise: Exercise

    @Before
    fun setUp() {
        testExercise = Exercise("Push Up", ExerciseType.REPS_ONLY)
    }

    @Test
    fun getSets() {
        assertEquals(1, testExercise.sets) //Each exercise has 1 set by default
    }

    @Test
    fun setSets() {
        testExercise.sets = 5
        assertEquals(5, testExercise.sets)
    }

    @Test
    fun getReps() {
        assertNull(testExercise.reps) //Reps is null on initialization
    }

    @Test
    fun setReps() {
        testExercise.reps = 8
        assertEquals(8, testExercise.reps)
    }

    @Test
    fun getWeight() {
        assertNull(testExercise.weight) //Weight is null on initialization
    }

    @Test
    fun setWeight() {
        testExercise.weight = 135
        assertEquals(135, testExercise.weight)
    }

    @Test
    fun getWeightStr() {
        assertNull(testExercise.weightStr) //Weight as a string is null on initialization
    }

    @Test
    fun setWeightStr() {
        testExercise.weightStr = "135lbs"
        assertEquals("135lbs", testExercise.weightStr)
    }

    @Test
    fun getDistance() {
        assertNull(testExercise.distance) //Distance is null on initialization
    }

    @Test
    fun setDistance() {
        testExercise.distance = 2.5
        assertEquals(2.5, testExercise.distance)
    }

    @Test
    fun getDuration() {
        assertNull(testExercise.duration) //Duration is null on initialization
    }

    @Test
    fun setDuration() {
        testExercise.duration = 60 //seconds
        assertEquals(60, testExercise.duration)
    }

    @Test
    fun copy() {
        val testExercise2 = testExercise.copy(name = "Plank", exerciseType = ExerciseType.DURATION_ONLY, duration = 90)
        assertNotSame(testExercise, testExercise2)
        assertEquals("Plank", testExercise2.name)
        assertEquals(ExerciseType.DURATION_ONLY, testExercise2.exerciseType)
        assertEquals(90, testExercise2.duration)
    }

    @Test
    fun getName() {
        assertEquals("Push Up", testExercise.name)
    }

    @Test
    fun getExerciseType() {
        assertEquals(ExerciseType.REPS_ONLY, testExercise.exerciseType)
    }

}