package com.swolebro.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.swolebro.R
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.internal.throwMissingFieldException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import com.swolebro.view.MainActivity
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore, context: Context) {

    private lateinit var currentUser: User
    var context = context
    var globalStreakGoal = 0;
    fun translateUser(name: String, firebaseUser: FirebaseUser): User {
        currentUser = User(email=firebaseUser.email, name=name, userId=firebaseUser.uid)
        return currentUser
    }

    fun getCurrentUserSnapshot(): User{
        return currentUser
    }

    fun restoreCurrentUser(user: User){
        currentUser = user
    }

    fun createUser(email: String, name: String, password: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{creation ->
                    if (creation.isSuccessful){
                        val firebaseUser = auth.currentUser
                        val user = firebaseUser?.let { translateUser(name, it) }

                        if (user != null) {
                            storeUser(user)
                            onResult(Result.success(firebaseUser))
                        }
                        else{
                            onResult(Result.failure(Exception("User is null, issue with user translation")))
                        }
                    } else {
                        onResult(Result.failure(creation.exception ?: Exception("Authentication failure")))
                    }
                }
    }

    fun signIn(email: String, password: String, onResult: (Result<User>) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { login ->
                    if (login.isSuccessful && login.result.user != null) {
                        translateUser("", login.result!!.user!!)
                        onResult(Result.success(currentUser))
                    } else {
                        onResult(Result.failure(login.exception ?: Exception("Auth Failure")))
                    }
                }
    }

    suspend fun signInNew(email: String, password: String): Result<User> = suspendCoroutine{ continuation ->
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { login ->
                    if (login.isSuccessful && login.result.user != null) {
                        translateUser("", login.result!!.user!!)
                        continuation.resume(Result.success(currentUser))
                    } else {
                        continuation.resume(Result.failure(login.exception ?: Exception("Auth Failure")))
                    }
                }
    }

    fun signInGoogle(idToken: String, onResult: (Result<User>) -> Unit){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener { login ->
                    if (login.isSuccessful && login.result.user != null){
                        val user = translateUser(login.result!!.user!!.displayName.toString(), login.result!!.user!!)
                        storeUser(user)
                        onResult(Result.success(currentUser))
                    } else{
                        onResult(Result.failure(login.exception ?: Exception("Auth Failure")))
                    }
                }
    }

    private fun storeUser(user: User){
        if (user.userId.isNotEmpty()) {
            db.collection("users").document(user.userId)
                    .set(user)
                    .addOnSuccessListener {
                        Log.d("Firestore", "User written successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding document", e)
                    }
        } else {
            Log.d("Firestore", "Error: userId is null or empty")
        }
    }

    suspend fun loadUserData(): User{
        Log.d("User Data", "Loading User Data")
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val name = documentSnapshot["name"].toString()
        val streak = documentSnapshot["streak"].toString()
        val height = documentSnapshot["height"].toString()
        val weight = documentSnapshot["weight"].toString()
        val bmi = documentSnapshot["bmi"].toString()
        val defaultUnits = documentSnapshot["defaultUnits"].toString()
        val streakGoal = documentSnapshot["streak_goal"].toString()
        val defaultTemplates = Workout.fromMap(documentSnapshot["default_templates"] as? List<Map<String, Any>> ?: listOf())
        val templates = Workout.fromMap(documentSnapshot["templates"] as? List<Map<String, Any>> ?: listOf())
        val workouts = Workout.fromMap(documentSnapshot["workouts"] as? List<Map<String, Any>> ?: listOf())
        currentUser.name = name
        currentUser.height = height
        currentUser.weight = weight
        currentUser.bmi = bmi
        currentUser.defaultUnits = defaultUnits
        currentUser.streak = streak.toInt()
        currentUser.streak_goal = streakGoal.toInt()
        currentUser.default_templates = defaultTemplates
        currentUser.templates = templates
        currentUser.workouts = workouts
        Log.d("User Data", "$currentUser")
        return currentUser
    }

    suspend fun getWorkouts(): MutableList<Workout> {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val workoutsMap = documentSnapshot["workouts"] as? List<Map<String, Any>> ?: listOf()
        val workouts = Workout.fromMap(workoutsMap)
        return workouts
    }

    suspend fun getTemplates(): MutableList<Workout> {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val templatesMap = documentSnapshot["templates"] as? List<Map<String, Any>> ?: listOf()
        val templates = Workout.fromMap(templatesMap)
        return templates
    }

    suspend fun getDefaultTemplates(): MutableList<Workout> {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val templatesMap = documentSnapshot["default_templates"] as? List<Map<String, Any>> ?: listOf()
        val templates = Workout.fromMap(templatesMap)
        return templates
    }

    suspend fun getIncompleteWorkout(): Workout? {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val workoutsMap = documentSnapshot["workouts"] as? List<Map<String, Any>> ?: listOf()
        val workouts = Workout.fromMap(workoutsMap)
        for (workout in workouts.reversed()) {
            // Loop backwards because it's probably the latest workout
            if (workout.isComplete == false) {
                return workout
            }
        }
        return null
    }

    suspend fun getName(): String {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val name = documentSnapshot["name"].toString()
        return name
    }

    fun updateName(updatedName: String) {
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("name", updatedName)
    }

    suspend fun getEmail(): String {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val email = documentSnapshot["email"].toString()
        return email
    }

    suspend fun getHeightWeight(): MutableList<String> {
        val hw: MutableList<String> = mutableListOf()
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val height = documentSnapshot["height"]
        val weight = documentSnapshot["weight"]
        val bmi = documentSnapshot["bmi"]
        hw.add(height.toString())
        hw.add(weight.toString())
        hw.add(bmi.toString())
        return hw
    }

    fun calculateBMI(height: String, weight: String): String{
        var bmi = ""
        BodyMassIndexCalc(height, weight) { value ->
            bmi = value
            val userRef = db.collection("users").document(currentUser.userId)
            userRef.update("bmi", value)
        }
        return bmi
    }

    suspend fun getStreak(): String {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val streak = documentSnapshot["streak"].toString()
        return streak
    }

    suspend fun getDefaultUnits(): String{
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val unit = documentSnapshot["defaultUnits"].toString()
        return unit
    }

    fun postUpdatedUnits(unit: String){
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("defaultUnits", unit)
    }

    fun postNewWorkout(workout: Workout){
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("workouts", FieldValue.arrayUnion(workout))
    }

    fun postNewTemplate(workout: Workout){
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("templates", FieldValue.arrayUnion(workout))
    }

    fun postNewDefaultTemplate(workout: Workout){
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("default_templates", FieldValue.arrayUnion(workout))
    }

    fun getAuth() : FirebaseAuth {
        return auth
    }

    fun postNewStreakGoal(streakGoal: Int) {
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("streak_goal", streakGoal)
        globalStreakGoal = streakGoal
    }

    fun postNewWeight(weight: String){
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("weight", weight)
    }

    fun postNewHeight(height: String){
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("height", height)
    }

    suspend fun getStreakGoal(): String {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val streakGoal = documentSnapshot["streak_goal"].toString()
        globalStreakGoal = streakGoal.toInt()
        return streakGoal
    }

    fun sendNotification(streak: Int, context: Context) {
        val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Streak Update")
                .setContentText("Your Streak has reached " + streak)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(globalStreakGoal, streak, false)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return
            }
            notify(1, builder.build())
        }
    }

    fun postUpdatedStreak(updatedStreak: Int) {
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("streak", updatedStreak)
    }

    suspend fun getStreakStart(): String {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val streak_start = documentSnapshot["streak_start"].toString()
        return streak_start
    }

    suspend fun getStreakEnd(): String {
        val documentRef = db.collection("users").document(currentUser.userId)
        val documentSnapshot = documentRef.get().await()
        val streak_end = documentSnapshot["streak_end"].toString()
        return streak_end
    }

    suspend fun updateStreak(): Int {
        val streak = getStreak()
        var currentStreak = if (streak != "null"){streak.toInt()}else{0}
        var streakGoal = getStreakGoal().toInt()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val startDate = dateFormat.parse(getStreakStart())
        val endDate = dateFormat.parse(getStreakEnd())
        val today = LocalDate.now()

        // if today's date is after the end date
        if (today.isAfter(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            // and if streak is less than streak goal, reset it to 0
            if (streakGoal > currentStreak) {
                currentStreak = 0
                postUpdatedStreak(currentStreak)
            }
            // reset start and end dates for the new week
            var sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY))
            var saturday = today.with(TemporalAdjusters.next(DayOfWeek.THURSDAY))
            updateStreakDates(sunday, saturday)
            globalStreakGoal = streakGoal
        }

        return currentStreak
    }

    suspend fun incrementStreak(): String {
        var streak = getStreak().toInt()
        ++streak
        postUpdatedStreak(streak)
        sendNotification(streak, context )
        return streak.toString()
    }

    fun updateStreakDates(sunday: LocalDate, saturday: LocalDate) {
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("streak_start", sunday.toString())
        userRef.update("streak_end", saturday.toString())
    }

    fun setActiveWorkout(workout: Workout?){
        currentUser.activeWorkout = workout
        val userRef = db.collection("users").document(currentUser.userId)
        userRef.update("activeWorkout", workout)
    }

    suspend fun getFriends(): MutableList<Triple<String, String, String>> {
        val friendsAndStreak = mutableListOf<Triple<String, String, String>>()

        val userRef = db.collection("users").document(currentUser.userId).get().await()
        val friendEmails = userRef["friends"] as? MutableList<String> ?: mutableListOf()
        for (email in friendEmails) {
            val friendDocSnapshot = db.collection("users").whereEqualTo("email", email).get().await()
            for (doc in friendDocSnapshot.documents) {
                val friendName = doc.getString("name") ?: "Unknown"
                val friendStreak = doc.get("streak").toString() ?: "0"
                friendsAndStreak.add(Triple(friendName, friendStreak, email))
            }
        }

        return friendsAndStreak
    }


    suspend fun getSentRequests(): MutableList<String> {
        val outgoingRequests = mutableListOf<String>()
        val userRef = db.collection("users").document(currentUser.userId).get().await()
        val emails = userRef["sentRequests"] as? MutableList<String> ?: emptyList()
        outgoingRequests.addAll(emails)

        return outgoingRequests
    }


    suspend fun getReceivedRequests(): MutableList<String> {

        val incomingRequests = mutableListOf<String>()
        val userRef = db.collection("users").document(currentUser.userId).get().await()
        val emails = userRef["receivedRequests"] as? MutableList<String> ?: emptyList()
        incomingRequests.addAll(emails)
        return incomingRequests
    }



    fun addFriend(email: String) {
        val currentUserRef = db.collection("users").document(currentUser.userId)
        if (email == currentUser.email ) {
            return
        }


        // Check if the recipient user exists and has received a friend request from the current user
        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val recipientUserId = document.id
                        val recipientRef = db.collection("users").document(recipientUserId)
                        // two cases, one you got a friend request from them, and u add them.
                        recipientRef.get().addOnSuccessListener { recipientDocument ->
                            val receivedRequests = recipientDocument["sentRequests"] as? List<String>
                            val existingFriends = recipientDocument["friends"] as? List<String>
                            if (existingFriends != null) {
                                if (currentUser.email.toString() in existingFriends) {

                                } else if (receivedRequests != null && currentUser.email in receivedRequests) {
                                    currentUserRef.update("friends", FieldValue.arrayUnion(email))
                                    // Just in case
                                    currentUserRef.update("receivedRequests", FieldValue.arrayRemove(email))
                                    currentUserRef.update("sentRequests", FieldValue.arrayRemove(email))
                                    recipientRef.update("friends", FieldValue.arrayUnion(currentUser.email))
                                    // Just in case
                                    recipientRef.update("receivedRequests", FieldValue.arrayRemove(currentUser.email))
                                    recipientRef.update("sentRequests", FieldValue.arrayRemove(currentUser.email))
                                } else {
                                    currentUserRef.update("sentRequests", FieldValue.arrayUnion(email))


                                    recipientRef.update("receivedRequests", FieldValue.arrayUnion(currentUser.email))
                                            .addOnSuccessListener {}
                                            .addOnFailureListener { e -> print("no friends for u :(") }
                                }
                            }
                        }.addOnFailureListener{}
                    }
                }

    }

    fun getNameByEmail(email: String, callback: (String) -> Unit) {
        // Check if the recipient user exists and has received a friend request from the current user
        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val recipientUserId = document.id
                        val recipientRef = db.collection("users").document(recipientUserId)
                        // two cases, one you got a friend request from them, and u add them.
                        recipientRef.get().addOnSuccessListener { recipientDocument ->
                            val name = (recipientDocument["name"] as? String).toString()
                            callback(name)
                        }
                    }
                }
    }

    fun getLatestWorkoutByEmail(email: String, callback: (Workout?) -> Unit) : Workout? {
        var returnedWorkout: Workout? = null

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val documents = db.collection("users").whereEqualTo("email", email).get().await()
                for (document in documents) {
                    val recipientUserId = document.id
                    val recipientRef = db.collection("users").document(recipientUserId)

                    val docSnapshot = recipientRef.get().await()
                    val workoutsMap = docSnapshot.get("workouts") as? List<Map<String, Any>> ?: listOf()
                    val workouts = Workout.fromMap(workoutsMap)
                    for (workout in workouts.reversed()) {
                        if (workout.isComplete == true) {
                            returnedWorkout = workout
                            break
                        }
                    }

                    if (returnedWorkout != null) {
                        break
                    }// Exit the loop if a workout has been found
                    else {
                        Log.d("IDK", "else statement")
                    }
                }
            } catch (e: Exception) {
                Log.d("Error fetching workouts", e.message ?: "An error occurred")
            }
            callback(returnedWorkout)
        }
        return returnedWorkout

    }



    fun removeFriend(email: String) {
        val currentUserRef = db.collection("users").document(currentUser.userId)


        // Check if the recipient user exists and has received a friend request from the current user
        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val recipientUserId = document.id
                        val recipientRef = db.collection("users").document(recipientUserId)
                        currentUserRef.update("friends", FieldValue.arrayRemove(email))
                                .addOnSuccessListener {
                                    recipientRef.update("friends", FieldValue.arrayRemove(currentUser.email))
                                }
                                .addOnFailureListener {
                                    Log.d("Remove friend not workign","Friend not found")
                                }
                    }
                    currentUserRef.update("receivedRequests", FieldValue.arrayRemove(email))
                    currentUserRef.update("sentRequests", FieldValue.arrayRemove(email))

                }

    }

    fun hasActiveWorkout(): Boolean {
        return (currentUser.activeWorkout != null)
    }
}