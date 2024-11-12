package com.swolebro.view

fun formatTime(timeInMs: Long): String {
    val totalSeconds = timeInMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun nameValidator(name: String): String {return if (name.isBlank()) "Name cannot be empty" else ""}
fun emailValidator(email: String): String {return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid email address" else ""}
fun passwordValidator(formSubmitted: Boolean, password: String): String {
    return if (formSubmitted && !password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$".toRegex())) "Password must contain at least 8 characters, one uppercase, and one number" else ""
}