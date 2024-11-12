package com.swolebro.view

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import com.swolebro.view.ui.theme.SwolebroTheme
import com.swolebro.model.UserRepository
import com.swolebro.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.swolebro.R
import com.swolebro.viewmodel.WorkoutViewModel
import com.swolebro.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var repository: UserRepository
    private lateinit var userViewModel: UserViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var gsiHelper: GoogleSignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    userViewModel.setIdToken(account.idToken)
                } catch (e: ApiException) {
                    // Handle exception
                }
            }
        }

        createNotificationChannel()
        val sharedPrefs = getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("NotificationsEnabled", true).apply()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        repository = UserRepository(auth, db, this)
        userViewModel = UserViewModel(repository)
        workoutViewModel = WorkoutViewModel(repository)
        gsiHelper = GoogleSignInHelper(this, signInLauncher, userViewModel) //DON'T REMOVE

        var existingUser: User? = null
        savedInstanceState?.let{
            val uid = it.getString("User_ID")
            val email = it.getString("User_Email")
            val name = it.getString("User_Name")
            val streak = it.getInt("User_Streak")
            val units = it.getString("User_Units")
            val weight = it.getString("User_Weight")
            val height = it.getString("User_Height")
            val bmi = it.getString("User_Bmi")
            val streak_start = it.getString("User_StreakStart")
            val streak_end = it.getString("User_StreakEnd")
            val streak_goal = it.getInt("User_StreakGoal")

            existingUser = User(
                userId = uid!!,
                email = email,
                name = name,
                streak = streak,
                defaultUnits = units!!,
                weight = weight!!,
                height = height!!,
                bmi = bmi!!,
                streak_start = streak_start!!,
                streak_end = streak_end!!,
                streak_goal = streak_goal
            )
        }

        if (existingUser != null){
            repository.restoreCurrentUser(existingUser!!)
        }

        setContent {
            SwolebroTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    App(userViewModel, workoutViewModel, gsiHelper, userViewModel::signUp)
                }
            }
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Notifications"
            val descriptionText = "Notifications for streaks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




    override fun onPause() {
        super.onPause()
        workoutViewModel.pauseWorkout()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val user = repository.getCurrentUserSnapshot()
        user.let{outState.putString("User_ID", it.userId)}
        user.let{outState.putString("User_Email", it.email)}
        user.let{outState.putString("User_Name", it.name)}
        user.let{outState.putInt("User_Streak", it.streak)}
        user.let{outState.putString("User_Units", it.defaultUnits)}
        user.let{outState.putString("User_Weight", it.weight)}
        user.let{outState.putString("User_Height", it.height)}
        user.let{outState.putString("User_Bmi", it.bmi)}
        user.let{outState.putString("User_StreakStart", it.streak_start)}
        user.let{outState.putString("User_StreakEnd", it.streak_end)}
        user.let{outState.putInt("User_StreakGoal", it.streak_goal)}
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}