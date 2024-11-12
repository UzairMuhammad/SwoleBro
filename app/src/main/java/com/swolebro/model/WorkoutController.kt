package com.swolebro.model;
import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthenticationException
import kotlinx.coroutines.runBlocking
import java.util.*

class WorkoutController (workout: Workout, userRepository: UserRepository) {

    val repository = userRepository

    var workoutStarted = false
    var workoutPaused = false
    var activeWorkout: Workout? = null
    private var timer: Timer? = null
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var totalPausedTime = 0L
    private var pauseStartTime = 0L

    init {
        // Check if valid user
        if (userRepository.getAuth().currentUser == null) {
            throw AuthenticationException("Not an authenticated user")
        }
        runBlocking {
            activeWorkout = repository.getIncompleteWorkout()
        }
        if (activeWorkout == null) {
            println("There is no previously incomplete workout, new controller can be created")
            activeWorkout = workout
        }
    }


    // Start a workout
    fun startWorkout(){
        if (!workoutStarted) {
            repository.setActiveWorkout(activeWorkout)
            startTime = System.currentTimeMillis()
            workoutStarted = true
            workoutPaused = false
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    elapsedTime = System.currentTimeMillis() - startTime - totalPausedTime
                }
            }, 0, 1000)
        }
    }

    // Pause a running workout
    fun pauseWorkout() {
        if (!workoutPaused && workoutStarted) {
           workoutPaused = true
           pauseStartTime = System.currentTimeMillis()
        }
    }

    fun resumeWorkout(){
        if (workoutPaused){
            totalPausedTime += System.currentTimeMillis() - pauseStartTime
            elapsedTime = System.currentTimeMillis() - startTime - totalPausedTime
            pauseStartTime = 0L
            workoutPaused = false
        }
    }

    fun endWorkout(workout: Workout) {
        if (workoutStarted) {
            workout.isComplete = true
            save() // Workout gets posted -> in Workout.kt using logTemplate
            workoutPaused = false
            workoutStarted = false
            activeWorkout = null
            repository.setActiveWorkout(activeWorkout)
        }
    }

    fun getWorkoutTime(): Long {
        return elapsedTime
    }

    private fun save() {
        // Save the current state
        activeWorkout?.let { repository.postNewWorkout(it) }
    }
}

