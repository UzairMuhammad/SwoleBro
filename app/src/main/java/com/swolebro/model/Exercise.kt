package com.swolebro.model

enum class ExerciseType {
    WEIGHTS,
    DISTANCE_AND_DURATION,
    DURATION_ONLY,
    REPS_ONLY,
    STRETCH_ONLY,
}

open class Exercise(val name: String, val exerciseType: ExerciseType) {
    // Assume every exercise has at least one set
    var sets = 1;

    // Optional
    var reps: Int? = null
    var weight: Int? = null
     var weightStr: String? = null
    var distance: Double? = null
     var duration: Int? = null

     fun copy(
         name: String = this.name,
         exerciseType: ExerciseType = this.exerciseType,
         sets: Int = this.sets,
         reps: Int? = this.reps,
         weight: Int? = this.weight,
         weightStr: String? = this.weightStr,
         distance: Double? = this.distance,
         duration: Int? = this.duration
     ): Exercise {
         val newExercise = Exercise(name, exerciseType)
         newExercise.sets = sets
         newExercise.reps = reps
         newExercise.weight = weight
         newExercise.weightStr = weightStr
         newExercise.distance = distance
         newExercise.duration = duration
         return newExercise
     }

     companion object {
         fun fromMap(map: Map<String, Any>): Exercise {
             val name = map["name"] as String
             val exerciseType = ExerciseType.valueOf(map["exerciseType"] as String)
             val exercise = Exercise(name, exerciseType)
             exercise.sets = (map["sets"] as? Number)?.toInt() ?: 1
             exercise.reps = (map["reps"] as? Number)?.toInt()
             exercise.weight = (map["weight"] as? Number)?.toInt()
             exercise.weightStr = (map["weightStr"] as? String)?.toString()
             exercise.distance = (map["distance"] as? Number)?.toDouble()
             exercise.duration = (map["duration"] as? Number)?.toInt()
             return exercise
         }
     }

}