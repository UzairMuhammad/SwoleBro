package com.swolebro.view.Workouts

import CustomSurface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swolebro.model.Exercise
import com.swolebro.model.ExerciseType
import com.swolebro.view.ExerciseCardPreview
import com.swolebro.view.TemplateExercisesCardPreview
import com.swolebro.viewmodel.WorkoutViewModel

@Composable
fun TemplatePreviewDialog(onDismiss: () -> Unit, templateName: String, exercises: MutableList<Exercise>, workoutViewModel: WorkoutViewModel){
    var start by remember { mutableStateOf(false) }
    var existingActiveWorkout by remember { mutableStateOf(false) }

    if (!start) {
        Dialog(onDismissRequest = onDismiss) {
            CustomSurface() {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        templateName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TemplateExercisesCardPreview(exercises)
                    Spacer(modifier = Modifier.height(12.dp))

                    ExtendedFloatingActionButton(
                        onClick = {
                            val workoutInstance = com.swolebro.model.Workout()
                            workoutInstance.templateName = templateName
                            workoutInstance.exercises = exercises
                            if (workoutViewModel.workoutInProgress()){
                                existingActiveWorkout = true
                            }
                            else {
                                workoutViewModel.beginWorkout(workoutInstance)
                                start = true
                            }
                        },
                        icon = {Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "Start Workout",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )},
                        text = { Text(text = "Start", color = MaterialTheme.colorScheme.primary) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                }
            }
        }
    }
    else{
        ActiveWorkoutDialog(onDismiss, templateName, exercises, workoutViewModel)
    }

    if (existingActiveWorkout){
        HandleActiveWorkout(workoutViewModel, onDismiss)
    }
}
