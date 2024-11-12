package com.swolebro.viewmodel

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swolebro.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class WorkoutViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _streak = MutableLiveData<String>()
    val streak: LiveData<String> = _streak

    private val _workoutUnits = MutableLiveData<String>()
    val workoutUnits: LiveData<String> = _workoutUnits

    var workoutController: WorkoutController? = null
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    fun createTemplate(name: String, templateExercises: MutableList<Exercise>){
        val newTemplate = Workout()
        newTemplate.templateName = name
        newTemplate.templateId = UUID.randomUUID().toString()
        newTemplate.exercises = templateExercises
        userRepository.postNewTemplate(newTemplate)
    }

    fun logTemplate(name: String, loggedExercises: MutableList<Exercise>, note: String){
        workoutController?.activeWorkout?.exercises = loggedExercises
        workoutController?.activeWorkout?.note = note
        endWorkout()
    }

    fun beginWorkout(workout: Workout){
        if (workoutController == null || workoutController?.activeWorkout == null) {
            workoutController = WorkoutController(workout, userRepository)
        }

        workoutController?.activeWorkout = workout
        workoutController?.startWorkout()

        viewModelScope.launch {
            while (workoutController?.workoutStarted == true) {
                if (!workoutController?.workoutPaused!!) {
                    _elapsedTime.value = workoutController?.getWorkoutTime() ?: 0
                    delay(1000) // Update every second
                } else {
                    break
                }
            }
        }
    }

    fun pauseWorkout(){
        workoutController?.pauseWorkout()
    }

    fun resumeWorkout(){
        workoutController?.resumeWorkout()
        beginWorkout(workoutController?.activeWorkout ?: return)
    }

    fun cancelWorkout() {
        workoutController?.pauseWorkout()
        _elapsedTime.value = 0L // Reset the timer
        workoutController = null
        userRepository.setActiveWorkout(null)
    }

    fun endWorkout() {
        val totalTime = workoutController?.getWorkoutTime() ?: 0L
        workoutController?.activeWorkout?.totalDuration = totalTime
        workoutController?.endWorkout(workoutController?.activeWorkout!!)
        _elapsedTime.value = totalTime
        workoutController = null
    }

    fun getStreak() {
        viewModelScope.launch {
            _streak.value = userRepository.getStreak()
        }
    }

    fun incrementStreak() {
        viewModelScope.launch {
            _streak.value = userRepository.incrementStreak()
        }
    }

    fun workoutInProgress(): Boolean {
        return (workoutController != null && userRepository.hasActiveWorkout())
    }

    fun getActiveWorkout(): Workout? {
        return workoutController?.activeWorkout
    }

    fun getWorkoutUnits() {
        viewModelScope.launch {
            _workoutUnits.value = userRepository.getDefaultUnits()
        }
    }

    fun createDefaultBulkTemplate() {
        val newTemplate = Workout()
        newTemplate.templateName = "5x5 Workout A"
        newTemplate.templateId = UUID.randomUUID().toString()
        val exercises = mutableListOf<Exercise>()
        val squats = Exercise("Barbell Squats", ExerciseType.WEIGHTS)
        squats.sets = 5
        val benchpress = Exercise("Barbell Bench Press", ExerciseType.WEIGHTS)
        benchpress.sets = 5
        val bentoverrow = Exercise("Barbell Bent Over Row", ExerciseType.WEIGHTS)
        bentoverrow.sets = 5
        exercises.add(squats)
        exercises.add(benchpress)
        exercises.add(bentoverrow)
        newTemplate.exercises = exercises
        userRepository.postNewDefaultTemplate(newTemplate)

        exercises.clear()
        val newTemplate2 = Workout()
        newTemplate2.templateName = "5x5 Workout B"
        newTemplate2.templateId = UUID.randomUUID().toString()
        val overheadpress = Exercise("Barbell Overhead Press", ExerciseType.WEIGHTS)
        benchpress.sets = 5
        val deadlift = Exercise("Deadlift", ExerciseType.WEIGHTS)
        bentoverrow.sets = 5
        exercises.add(squats)
        exercises.add(overheadpress)
        exercises.add(deadlift)
        newTemplate2.exercises = exercises
        userRepository.postNewDefaultTemplate(newTemplate2)

        exercises.clear()
        val newTemplate3 = Workout()
        newTemplate3.templateName = "5x5 Workout C"
        newTemplate3.templateId = UUID.randomUUID().toString()
        val inclinedbenchpress = Exercise("Barbell Inclined Bench Press", ExerciseType.WEIGHTS)
        benchpress.sets = 5
        val pendlay = Exercise("Barbell Pendlay Row", ExerciseType.WEIGHTS)
        bentoverrow.sets = 5
        exercises.add(squats)
        exercises.add(inclinedbenchpress)
        exercises.add(pendlay)
        newTemplate3.exercises = exercises
        userRepository.postNewDefaultTemplate(newTemplate3)
    }

    fun createDefaultCutTemplate() {
        val newTemplate = Workout()
        newTemplate.templateName = "Cardio Ignite"
        newTemplate.templateId = UUID.randomUUID().toString()
        val exercises = mutableListOf<Exercise>()
        val elliptical = Exercise("Elliptical", ExerciseType.DISTANCE_AND_DURATION)
        val cycling = Exercise("Cycling", ExerciseType.DISTANCE_AND_DURATION)
        val rowing = Exercise("Rowing", ExerciseType.DISTANCE_AND_DURATION)
        exercises.add(elliptical)
        exercises.add(cycling)
        exercises.add(rowing)
        newTemplate.exercises = exercises
        userRepository.postNewDefaultTemplate(newTemplate)

        exercises.clear()
        val newTemplate2 = Workout()
        newTemplate2.templateName = "Cardio Blast"
        newTemplate2.templateId = UUID.randomUUID().toString()
        val treadmill = Exercise("Treadmill", ExerciseType.DISTANCE_AND_DURATION)
        val battleropes = Exercise("Battle Ropes", ExerciseType.DISTANCE_AND_DURATION)
        exercises.add(treadmill)
        exercises.add(cycling)
        exercises.add(battleropes)
        newTemplate2.exercises = exercises
        userRepository.postNewDefaultTemplate(newTemplate2)

        exercises.clear()
        val newTemplate3 = Workout()
        newTemplate3.templateName = "Cardio Core Fusion"
        newTemplate3.templateId = UUID.randomUUID().toString()
        val running = Exercise("Running", ExerciseType.DISTANCE_AND_DURATION)
        val crunches = Exercise("Crunches", ExerciseType.REPS_ONLY)
        val planks = Exercise("Planks", ExerciseType.DURATION_ONLY)
        exercises.add(running)
        exercises.add(crunches)
        exercises.add(planks)
        newTemplate3.exercises = exercises
        userRepository.postNewDefaultTemplate(newTemplate3)
    }
}