package com.swolebro.view.Workouts

import Streak
import TextWithShadow
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swolebro.R
import com.swolebro.model.*
import com.swolebro.view.ui.theme.GreyDark
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel

@Composable
fun Workout(navController: NavHostController, viewModel: UserViewModel, workoutViewModel: WorkoutViewModel, workoutTabData: Pair<Int, Pair<MutableList<Workout>, MutableList<Workout>>>) {
    val createTemplate = remember{ mutableStateOf(false) }

    val initialStreak = workoutTabData.first
    val customTemplates = workoutTabData.second.first
    val recommendedTemplates = workoutTabData.second.second

    var updatedStreak by remember { mutableIntStateOf(initialStreak) }
    val templates by remember { mutableStateOf(customTemplates) }
    val defaultTemplates by remember { mutableStateOf(recommendedTemplates) }

    val viewModelTemplates by viewModel.templates.observeAsState(templates)
    val viewModelDefaultTemplates by viewModel.defaultTemplates.observeAsState(defaultTemplates)
    val viewModelStreak by workoutViewModel.streak.observeAsState()

    // Update state when ViewModel's LiveData changes
    LaunchedEffect(viewModelTemplates) {
        viewModel.getTemplates()
    }

    LaunchedEffect(viewModelDefaultTemplates) {
        viewModel.getDefaultTemplates()
    }

    LaunchedEffect(viewModelStreak) {
        viewModelStreak?.let {
            // Assuming streak is a String that needs to be parsed into an Int
            updatedStreak = it.toIntOrNull() ?: updatedStreak
        }
    }

    /*
    val templates by viewModel.templates.observeAsState(emptyList<Workout>())
    LaunchedEffect(templates){
        viewModel.getTemplates()
    }

    val defaultTemplates by viewModel.defaultTemplates.observeAsState(emptyList<Workout>())
    LaunchedEffect(defaultTemplates){
        viewModel.getDefaultTemplates()
    }

    val streak by workoutViewModel.streak.observeAsState("")
    var updatedStreak by remember { mutableIntStateOf(-1) }
    LaunchedEffect(streak){
        if (streak.isEmpty()) {
            workoutViewModel.updateStreak()
        } else {
            workoutViewModel.updateStreak()
            updatedStreak = streak.toInt()
        }
    }
     */

    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Streak(updatedStreak)
        Text("Recommended Templates", style = MaterialTheme.typography.headlineMedium)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Replace this with premade templates
            var counter = 0;
            items(viewModelDefaultTemplates) {template ->
                if (counter > 3) {
                    counter = 0
                }
                WorkoutTemplateCard(template, workoutViewModel, counter)
                counter++
            }
        }
        var counter = 4
        Spacer(modifier = Modifier.height(16.dp))
        Text("Custom Templates", style = MaterialTheme.typography.headlineMedium)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (counter > 6) {
                counter = 4
            }
            items(viewModelTemplates) {template ->
                WorkoutTemplateCard(template, workoutViewModel, counter)
                counter++
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AddTemplateFloatingButton { createTemplate.value = true }
    }

    if (createTemplate.value) {
        CreateTemplateDialog(createTemplate = createTemplate, viewModel, workoutViewModel)
    }
}

@Composable
fun AddTemplateFloatingButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Add, "Create Template Button") },
        text = { Text(text = "New Template", color = MaterialTheme.colorScheme.secondary) },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}

@Composable
fun WorkoutTemplateCard(template: Workout, workoutViewModel: WorkoutViewModel, counter: Int) {
    var showDialog by remember { mutableStateOf(false) }
    Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable { showDialog = true },
    ) {
        Card(
                modifier = Modifier
                        .width(160.dp)
                        .height(96.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Set to a base or surface color if the image doesn't cover
        ) {
            Box {
                // Assuming you have an image named workout_background in your drawable resources
                var picture = when(counter) {
                    0 -> R.drawable.workout0
                    1 -> R.drawable.workout1
                    2 -> R.drawable.workout2
                    3 -> R.drawable.workout3
                    4 -> R.drawable.workout5
                    5 -> R.drawable.workout7
                    6 -> R.drawable.workout6
                    7 -> R.drawable.workout4
                    else -> R.drawable.workout0
                }
                picture?.let { painterResource(id = it) }?.let {
                    Image(
                            painter = it,
                            contentDescription = "Background",
                            contentScale = ContentScale.Crop, // Fill the card maintaining aspect ratio
                            modifier = Modifier.fillMaxSize()
                    )
                }
                // Text overlays within the Box, adjusting padding as needed
                Column(
                        modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TextWithShadow(
                        text = template.templateName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                    )
                    TextWithShadow(
                        text = "${template.exercises.size} exercises",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreyDark
                    )
                }
            }
        }
    }

    // Display the dialog when showDialog is true
    if (showDialog) {
        TemplatePreviewDialog(
                onDismiss = {
                    showDialog = false
                    workoutViewModel.pauseWorkout()
                },
                templateName = template.templateName,
                exercises = template.exercises,
                workoutViewModel = workoutViewModel
        )
    }
}
