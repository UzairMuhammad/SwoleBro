package com.swolebro.view.Workouts

import CustomSurface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.swolebro.model.Exercise
import com.swolebro.model.ExerciseType
import com.swolebro.view.formatTime
import com.swolebro.viewmodel.WorkoutViewModel
import kotlinx.coroutines.InternalCoroutinesApi


@Composable
fun NumberStepper(label: String, value: Int, increments: Int, onValueChange: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        ){
            Icon(
                Icons.Filled.Remove,
                contentDescription = "Decrease",
                modifier = Modifier.clickable{if (value > 0) onValueChange(value - increments)}
            )
        }
        Text(
            "$value",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Increase",
                modifier = Modifier.clickable { onValueChange(value + increments) }
            )
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ActiveWorkoutDialog(onDismiss: () -> Unit, templateName: String, exercises: MutableList<Exercise>, workoutViewModel: WorkoutViewModel) {

    val modifiedExercises = remember { mutableStateListOf<Exercise>().also { it.addAll(exercises) } }
    val workoutUnits by workoutViewModel.workoutUnits.observeAsState()
    val elapsedTime by workoutViewModel.elapsedTime.collectAsState()

    LaunchedEffect(workoutUnits){
        workoutViewModel.getWorkoutUnits()
    }

    val formattedTime = remember(elapsedTime) {
        formatTime(elapsedTime)
    }

    Dialog(onDismissRequest = onDismiss) {
        CustomSurface() {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = {
                        workoutViewModel.cancelWorkout()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
                Text(
                    templateName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                ){
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(0.dp, 384.dp).fillMaxWidth().padding(8.dp)
                    ) {
                        itemsIndexed(modifiedExercises) {index, exercise ->
                            when (exercise.exerciseType){
                                ExerciseType.WEIGHTS -> StrengthTemplateExercise(exercise, workoutUnits) { updatedExercise -> modifiedExercises[index] = updatedExercise}
                                ExerciseType.DISTANCE_AND_DURATION -> DurationDistanceTemplateExercise(exercise) { updatedExercise -> modifiedExercises[index] = updatedExercise}
                                ExerciseType.DURATION_ONLY -> DurationOnlyTemplateExercise(exercise) { updatedExercise -> modifiedExercises[index] = updatedExercise}
                                ExerciseType.REPS_ONLY -> RepsOnlyTemplateExercise(exercise) { updatedExercise -> modifiedExercises[index] = updatedExercise}
                                ExerciseType.STRETCH_ONLY -> DurationOnlyTemplateExercise(exercise) { updatedExercise -> modifiedExercises[index] = updatedExercise}
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                var note by remember { mutableStateOf("") }
                var addNote by remember { mutableStateOf(false) }
                TextButton(
                    onClick = {
                        addNote = true
                    }
                ) {
                    Text("Add a note")
                }
                if (addNote) {
                    Dialog(onDismissRequest = onDismiss) {
                        CustomSurface() {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    label = { Text("Add a note to this workout") },
                                )
                                Button(
                                    onClick = {
                                        addNote = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Add Note to Workout")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Time Elapsed: $formattedTime",
                    letterSpacing = 3.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        workoutViewModel.logTemplate(templateName, modifiedExercises, note)
                        workoutViewModel.incrementStreak()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Log Workout")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandleActiveWorkout(workoutViewModel: WorkoutViewModel, onDismiss: () -> Unit){

    val openActiveWorkoutAlert = remember { mutableStateOf(true) }
    val activeWorkout = workoutViewModel.getActiveWorkout()
    val loadActiveWorkout = remember { mutableStateOf(false) }

    when {
        openActiveWorkoutAlert.value -> {
            AlertDialog(
                icon = {
                    Icon(Icons.Filled.Timer, contentDescription = "Active Workout", tint = Color.Black)
                },
                title = {
                    Text(text = "Workout in Progress")
                },
                text = {
                    Text(text = "You have an incomplete workout that was started previously, Would you like to resume?")
                },
                onDismissRequest = {
                    openActiveWorkoutAlert.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            loadActiveWorkout.value = true
                            openActiveWorkoutAlert.value = false
                        }
                    ) {
                        Text("Resume")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            workoutViewModel.cancelWorkout()
                            openActiveWorkoutAlert.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        loadActiveWorkout.value -> {
            if (activeWorkout != null) {
                workoutViewModel.resumeWorkout()
                ActiveWorkoutDialog(onDismiss, activeWorkout.templateName, activeWorkout.exercises, workoutViewModel)
            }
        }
    }
}

@Composable
fun StrengthTemplateExercise(exercise: Exercise, unit: String?, onExerciseUpdate: (Exercise) -> Unit){
    var sets by remember {mutableStateOf(exercise.sets)}
    var reps by remember {mutableStateOf(exercise.reps ?: 0)}
    var weight by remember {mutableStateOf(exercise.weight ?: 0)}

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(
            exercise.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        NumberStepper(label = "Sets", value = sets, increments = 1, onValueChange = {
            sets = it
            onExerciseUpdate(exercise.copy(sets = it))
        })
        NumberStepper(label = "Reps", value = reps, increments = 1, onValueChange = {
            reps = it
            onExerciseUpdate(exercise.copy(reps = it))
        })
        NumberStepper(label = "Weight ($unit)", value = weight, increments = 10, onValueChange = {
            weight = it
            onExerciseUpdate(exercise.copy(weight = it))
            onExerciseUpdate(exercise.copy(weightStr = it.toString() + unit))
        })
    }
}

@Composable
fun DurationDistanceTemplateExercise(exercise: Exercise, onExerciseUpdate: (Exercise) -> Unit){
    var duration by remember {mutableStateOf(exercise.duration ?: 0)}
    var distance by remember {mutableStateOf(exercise.distance ?: 0)}

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(exercise.name, style = MaterialTheme.typography.labelMedium)
        NumberStepper(label = "Duration", value = duration, increments = 1, onValueChange = {
            duration = it
            onExerciseUpdate(exercise.copy(duration = it))
        })
        NumberStepper(label = "Distance", value = distance.toInt(), increments = 1, onValueChange = {
            distance = it
            onExerciseUpdate(exercise.copy(distance = it.toDouble()))
        })
    }

}

@Composable
fun DurationOnlyTemplateExercise(exercise: Exercise, onExerciseUpdate: (Exercise) -> Unit){
    var duration by remember {mutableStateOf(exercise.duration ?: 0)}

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(exercise.name, style = MaterialTheme.typography.labelMedium)
        NumberStepper(label = "Duration", value = duration, increments = 1, onValueChange = {
            duration = it
            onExerciseUpdate(exercise.copy(duration = it))
        })
    }
}

@Composable
fun RepsOnlyTemplateExercise(exercise: Exercise, onExerciseUpdate: (Exercise) -> Unit){
    var reps by remember {mutableStateOf(exercise.reps ?: 0)}

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(exercise.name, style = MaterialTheme.typography.labelMedium)
        NumberStepper(label = "Reps", value = reps, increments = 1, onValueChange = {
            reps = it
            onExerciseUpdate(exercise.copy(reps = it))
        })
    }
}
