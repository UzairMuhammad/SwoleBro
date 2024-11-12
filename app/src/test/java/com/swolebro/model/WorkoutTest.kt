package com.swolebro.model

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.time.LocalDate

class WorkoutTest {

    private lateinit var testWorkout: Workout

    @Before
    fun setup(){
        testWorkout = Workout()
    }

    @Test
    fun getDate() {
        val expectedDate = LocalDate.now().toString() //new Workout always initialized with today's date
        val actualDate = testWorkout.date
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun setDate() {
        val newDate = LocalDate.now().minusDays(1).toString()
        testWorkout.date = newDate
        assertEquals(newDate, testWorkout.date)
    }

    @Test
    fun getExercises() {
        assertTrue(testWorkout.exercises.isEmpty()) //Empty on initialization
    }

    @Test
    fun setExercises() {
        val newExercise = Exercise("Push Ups", ExerciseType.REPS_ONLY)
        val testExercises = mutableListOf(newExercise)

        testWorkout.exercises = testExercises
        assertEquals(testExercises, testWorkout.exercises)
        assertTrue(testWorkout.exercises.contains(newExercise))
    }

    @Test
    fun getType() {
        assertNull(testWorkout.type) //Null on initialization
    }

    @Test
    fun setType() {
        val newType = ExerciseType.WEIGHTS
        testWorkout.type = newType
        assertEquals(newType, testWorkout.type)

    }

    @Test
    fun isComplete() {
        assertFalse(testWorkout.isComplete) //incomplete/false on initialization
    }

    @Test
    fun setComplete() {
        testWorkout.isComplete = true
        assertTrue(testWorkout.isComplete)
    }

    @Test
    fun getTemplateName() {
        assertTrue(testWorkout.templateName.isEmpty()) //empty on initialization
    }

    @Test
    fun setTemplateName() {
        val newTemplateName = "Leg Day"
        testWorkout.templateName = newTemplateName
        assertEquals(newTemplateName, testWorkout.templateName)
    }

    @Test
    fun getTemplateId() {
        assertNull(testWorkout.templateId) //null on initialization
    }

    @Test
    fun setTemplateId() {
        val testTemplateId = "swolePlan1"
        testWorkout.templateId = testTemplateId
        assertEquals(testTemplateId, testWorkout.templateId)
    }

    @Test
    fun getTotalDuration() {
        assertEquals(0, testWorkout.totalDuration) //0 on initialization
    }

    @Test
    fun setTotalDuration() {
        val testTotalDuration = 60L
        testWorkout.totalDuration = testTotalDuration
        assertEquals(testTotalDuration, testWorkout.totalDuration)
    }

    @Test
    fun getNote() {
        assertTrue(testWorkout.note.isEmpty()) //empty on initialization
    }

    @Test
    fun setNote() {
        val newNote = "Hit a new PR today!"
        testWorkout.note = newNote
        assertEquals(newNote, testWorkout.note)
    }
}