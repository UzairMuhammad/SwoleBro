package com.swolebro.view

import Social
import TopBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.swolebro.model.User
import com.swolebro.view.Workouts.Workout
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel
import kotlin.reflect.KFunction4

@Composable
fun NavBar(navController: NavHostController, selected: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AddHome, contentDescription = "Workout") },
            label = { Text("Workout", style = MaterialTheme.typography.labelSmall) },
            selected = selected == "workout",
            onClick = {
                navController.navigate("workout")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.History, contentDescription = "History") },
            label = { Text("History", style = MaterialTheme.typography.labelSmall) },
            selected = selected == "history",
            onClick = { navController.navigate("history") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.SportsKabaddi, contentDescription = "Social") },
            label = { Text("Social", style = MaterialTheme.typography.labelSmall) },
            selected = selected == "social",
            onClick = { navController.navigate("social") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
            selected = selected == "profile",
            onClick = { navController.navigate("profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(userViewModel: UserViewModel, workoutViewModel: WorkoutViewModel, gsiHelper: GoogleSignInHelper, signUpSuccess: KFunction4<String, String, String, (Result<FirebaseUser>) -> Unit, Unit>) {
    val navController = rememberNavController()
    var currentUser: User? = null
    NavHost(navController = navController, startDestination = "signup") {
        composable("signup") {
            SignUpScreen(userViewModel, navController)
        }
        composable("login") {
            LoginScreen(userViewModel, navController, gsiHelper)
        }
        composable("workout") {
            currentUser = userViewModel.userSnapshot()
            val workoutTabData = Pair(currentUser!!.streak, Pair(currentUser!!.templates, currentUser!!.default_templates))
            Scaffold (
                topBar = {TopBar(currentUser?.name ?: "Temp")},
                bottomBar = {
                    NavBar(navController, "workout")
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    Workout(navController, userViewModel, workoutViewModel, workoutTabData)
                }
            }
        }
        composable("history") {
            currentUser = userViewModel.userSnapshot()
            val historyTabData = Pair(currentUser!!.streak, currentUser!!.workouts)
            Scaffold (
                topBar = {TopBar(currentUser?.name ?: "Temp")},
                bottomBar = {
                    NavBar(navController, "history")
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    History(userViewModel, workoutViewModel, historyTabData)
                }
            }
        }
        composable("social") {
            currentUser = userViewModel.userSnapshot()
            Scaffold (
                topBar = {TopBar(currentUser?.name ?: "Temp")},
                bottomBar = {
                    NavBar(navController, "social")
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    Social(userViewModel, workoutViewModel)
                }
            }
        }
        composable("profile") {
            currentUser = userViewModel.userSnapshot()
            Scaffold (
                topBar = {TopBar(currentUser?.name ?: "Temp")},
                bottomBar = {
                    NavBar(navController, "profile")
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    Profile(userViewModel, workoutViewModel)
                }
            }
        }
        composable("workout_plan") {
            WorkoutPlan(navController, userViewModel, workoutViewModel)
        }
    }
}