package com.swolebro.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class User(
    var userId: String,
    var email: String?,
    var name: String? = null,
    var streak: Int = 0,
    var streak_goal: Int = 3,
    var streak_start: String = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).toString(),
    var streak_end: String = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString(),
    var defaultUnits: String = "kg",
    var weight: String = "",
    var height: String = "",
    var bmi: String = "",
    val friends: MutableList<User> = mutableListOf<User>(),
    val sentRequests: MutableList<User> = mutableListOf<User>(),
    val receivedRequests: MutableList<User> = mutableListOf<User>(),
    var activeWorkout: Workout? = null,
    var default_templates: MutableList<Workout> = mutableListOf(),
    var templates: MutableList<Workout> = mutableListOf(),
    var workouts: MutableList<Workout> = mutableListOf() //history
)