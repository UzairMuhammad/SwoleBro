package com.swolebro.model

import java.time.*
import java.util.*

/* These describe the various types of exercises */
class Workout {
    // Used for streaks
    var date = LocalDate.now().toString()
    var exercises = mutableListOf<Exercise>()
    var type: ExerciseType? = null
    var isComplete = false;
    var templateName: String = ""
    var templateId: String? = null
    var totalDuration: Long = 0
    var note: String = ""

    companion object {
        fun fromMap(listMap: List<Map<String, Any>>): MutableList<Workout> {
            val workouts = mutableListOf<Workout>()
            for (map in listMap){
                val workout = Workout()
                workout.type = map["type"]?.let { ExerciseType.valueOf(it as String) }
                workout.templateName = map["templateName"] as? String ?: "Template"
                workout.isComplete = map["complete"] as? Boolean ?: false
                workout.date = map["date"] as? String ?: LocalDate.now().toString()
                workout.totalDuration = map["totalDuration"] as? Long ?: 0
                //workout.templateId = map["templateId"] as String
                workout.note = map["note"] as? String ?: ""
                (map["exercises"] as? List<Map<String, Any>>)?.forEach {
                    workout.exercises.add(Exercise.fromMap(it))
                }
                workouts.add(workout)
            }
            return workouts
        }
    }
}