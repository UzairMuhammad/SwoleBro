package com.swolebro.view

import CustomSurface
import Streak
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.swolebro.R
import com.swolebro.model.Workout
import com.swolebro.view.ui.theme.Neons
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun History(viewModel: UserViewModel, workoutViewModel: WorkoutViewModel, historyTabData: Pair<Int, List<Workout>>) {

    val initialStreak = historyTabData.first
    val initialHistory = historyTabData.second

    var updatedStreak by remember { mutableIntStateOf(initialStreak) }
    var workouts by remember { mutableStateOf(initialHistory) }

    val viewModelWorkouts by viewModel.fullWorkouts.observeAsState(workouts)
    val viewModelStreak by workoutViewModel.streak.observeAsState()

    LaunchedEffect(viewModelWorkouts) {
        viewModel.getUserWorkouts()
    }

    LaunchedEffect(viewModelStreak) {
        viewModelStreak?.let {
            updatedStreak = it.toIntOrNull() ?: updatedStreak
        }
    }

    Column (
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Streak(updatedStreak)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            viewModelWorkouts?.let {
                items(it.reversed()){ workout ->
                    if (workout.isComplete) {
                        WorkoutCardNew(workout)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCardNew(workout: Workout) {
    val showDialog = remember { mutableStateOf(false) }

    Column(){
        Row(verticalAlignment = Alignment.Bottom){
            Text(
                "${workout.date}, ",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Text(
                workout.templateName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(
                text = "Total Duration: ",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = formatTime(workout.totalDuration),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
                letterSpacing = 2.sp
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().height(128.dp).padding(10.dp)
        ){
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Neons[0],
                ),
                onClick = { showDialog.value = true }
            ){
                Box {
                    var picture = R.drawable.workout0
                    picture?.let { painterResource(id = it) }?.let {
                        Image(
                                painter = it,
                                contentDescription = "bg",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                userScrollEnabled = false
            ){
                items(workout.exercises) {exercise ->
                    Text(exercise.name, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showDialog.value) {
        TemplateDialog(showDialog = showDialog, workout)
    }
}

@Composable
fun TemplateDialog(showDialog: MutableState<Boolean>, workout: Workout) {
    Dialog(onDismissRequest = { showDialog.value = false } ) {
        CustomSurface {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    workout.templateName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                TemplateExercisesCardPreview(workout.exercises, true)
                if (workout.note.isNotEmpty()) {
                    Text(
                        "Notes: " + workout.note,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}
