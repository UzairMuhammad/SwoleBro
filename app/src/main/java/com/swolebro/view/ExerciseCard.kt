package com.swolebro.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swolebro.model.Exercise
import com.swolebro.model.ExerciseType

@Composable
fun TemplateExercisesCardPreview(
    exercises: MutableList<Exercise>,
    detailed: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
    ){
        LazyColumn(
            modifier = Modifier.heightIn(0.dp, 384.dp).fillMaxWidth()
        ) {
            items(exercises) {exercise ->
                ExerciseCardPreview(exercise, detailed)
            }
        }
    }
}

@Composable
fun ExerciseCardPreview(exercise: Exercise, detailed: Boolean) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        Column(
            modifier = Modifier.padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (detailed) {
                when (exercise.exerciseType) {
                    ExerciseType.WEIGHTS -> StrengthCard(exercise)
                    ExerciseType.DISTANCE_AND_DURATION -> DistanceAndDurationCard(exercise)
                    ExerciseType.DURATION_ONLY -> DurationOrStretchCard(exercise)
                    ExerciseType.REPS_ONLY -> RepsCard(exercise)
                    ExerciseType.STRETCH_ONLY -> DurationOrStretchCard(exercise)
                }
            } else {
                ExerciseCardText("Type", exercise)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthCard(exercise: Exercise) {
    ExerciseCardText("Name", exercise)
    ExerciseCardText("Type", exercise)
    ExerciseCardText("Sets", exercise)
    ExerciseCardText("Reps", exercise)
    ExerciseCardText("Weight", exercise)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceAndDurationCard(exercise: Exercise) {
    ExerciseCardText("Name", exercise)
    ExerciseCardText("Type", exercise)
    ExerciseCardText("Duration", exercise)
    ExerciseCardText("Distance", exercise)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationOrStretchCard(exercise: Exercise) {
    ExerciseCardText("Name", exercise)
    ExerciseCardText("Type", exercise)
    ExerciseCardText("Duration", exercise)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepsCard(exercise: Exercise) {
    ExerciseCardText("Name", exercise)
    ExerciseCardText("Type", exercise)
    ExerciseCardText("Reps", exercise)
}

@Composable
fun ExerciseCardText(type: String, exercise: Exercise) {
    val text = when (type) {
        "Name" -> "Name: ${exercise.name}"
        "Type" -> "Type: ${"${exercise.exerciseType}".replace("_", " ")}"
        "Sets" -> "Sets: ${exercise.sets}"
        "Reps" -> if (exercise.reps != null) "Reps: ${exercise.reps}" else null
        "Duration" -> if (exercise.duration != null) "Duration: ${exercise.duration}" else null
        "Distance" -> if (exercise.distance != null) "Distance: ${exercise.distance}" else null
        "Weight" -> if (exercise.weightStr != null) "Weight: ${exercise.weightStr}" else null
        else -> null
    }

    if (text == null) return

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}
