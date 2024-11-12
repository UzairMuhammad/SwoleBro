package com.swolebro.model

import org.junit.Test
import org.junit.Assert.*

class ExerciseTypeTest {

    @Test
    fun name() {
        assertEquals("WEIGHTS", ExerciseType.WEIGHTS.name)
        assertEquals("DISTANCE_AND_DURATION", ExerciseType.DISTANCE_AND_DURATION.name)
        assertEquals("DURATION_ONLY", ExerciseType.DURATION_ONLY.name)
        assertEquals("REPS_ONLY", ExerciseType.REPS_ONLY.name)
        assertEquals("STRETCH_ONLY", ExerciseType.STRETCH_ONLY.name)
    }

    @Test
    fun ordinal() {
        assertEquals(0, ExerciseType.WEIGHTS.ordinal)
        assertEquals(1, ExerciseType.DISTANCE_AND_DURATION.ordinal)
        assertEquals(2, ExerciseType.DURATION_ONLY.ordinal)
        assertEquals(3, ExerciseType.REPS_ONLY.ordinal)
        assertEquals(4, ExerciseType.STRETCH_ONLY.ordinal)
    }

    @Test
    fun testToString() {
        assertEquals("WEIGHTS", ExerciseType.WEIGHTS.toString())
        assertEquals("DISTANCE_AND_DURATION", ExerciseType.DISTANCE_AND_DURATION.toString())
        assertEquals("DURATION_ONLY", ExerciseType.DURATION_ONLY.toString())
        assertEquals("REPS_ONLY", ExerciseType.REPS_ONLY.toString())
        assertEquals("STRETCH_ONLY", ExerciseType.STRETCH_ONLY.toString())
    }

    @Test
    fun values() {
        val expectedValues = arrayOf(ExerciseType.WEIGHTS, ExerciseType.DISTANCE_AND_DURATION, ExerciseType.DURATION_ONLY, ExerciseType.REPS_ONLY, ExerciseType.STRETCH_ONLY)
        assertArrayEquals(expectedValues, ExerciseType.values())
    }

    @Test
    fun valueOf() {
        assertEquals(ExerciseType.WEIGHTS, ExerciseType.valueOf("WEIGHTS"))
        assertEquals(ExerciseType.DISTANCE_AND_DURATION, ExerciseType.valueOf("DISTANCE_AND_DURATION"))
        assertEquals(ExerciseType.DURATION_ONLY, ExerciseType.valueOf("DURATION_ONLY"))
        assertEquals(ExerciseType.REPS_ONLY, ExerciseType.valueOf("REPS_ONLY"))
        assertEquals(ExerciseType.STRETCH_ONLY, ExerciseType.valueOf("STRETCH_ONLY"))
    }

}