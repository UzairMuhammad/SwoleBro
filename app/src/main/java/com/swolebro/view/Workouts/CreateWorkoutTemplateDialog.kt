package com.swolebro.view.Workouts

import CustomOutlinedTextField
import CustomSurface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swolebro.model.Exercise
import com.swolebro.model.ExerciseType
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTemplateDialog(createTemplate: MutableState<Boolean>, userViewModel: UserViewModel, viewModel: WorkoutViewModel){
    var templateName by remember{ mutableStateOf("") }
    var exerciseName by remember{ mutableStateOf("") }
    var exerciseType by remember{ mutableStateOf(ExerciseType.WEIGHTS) }
    var exercises = remember { mutableStateListOf<Exercise>() }

    Dialog(onDismissRequest = {createTemplate.value = false}){
        CustomSurface {
            Column(modifier = Modifier
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "New Template",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium
                )
                CustomOutlinedTextField(
                    value = templateName,
                    onValueChange = { templateName = it },
                    label = "Template Name"
                )
                CustomOutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = "Exercise Name"
                )
                ExerciseTypeRadioGroup(exerciseType) { type -> exerciseType = type }
                Button(
                    onClick = {
                        if (exerciseName.isNotEmpty()) {
                            // Create an Exercise object from the user inputs
                            val newExercise = Exercise(exerciseName, exerciseType!!)
                            exercises.add(newExercise)
                            // Reset the name field and exercise type for next input
                            exerciseName = ""
                        }
                    }
                ) {
                    Text("Add Exercise")
                }
                Button(
                    onClick = {
                        if (templateName.isNotEmpty() && exercises.isNotEmpty()) {
                            viewModel.createTemplate(templateName, exercises)
                            userViewModel.getTemplates()
                            // Reset the template name and exercises list
                            templateName = ""
                            exercises.clear()
                        }
                    }
                ) {
                    Text("Create Template")
                }
                // List the exercises that will be added to the template
                if (exercises.isNotEmpty()) {
                    Text("Exercises in Template:")
                    exercises.forEach { exercise ->
                        Text(exercise.name)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTypeRadioGroup(selectedType: ExerciseType, onTypeSelected: (ExerciseType) -> Unit) {
    val exerciseTypeOptions = ExerciseType.entries.map { it.name }
    var selectedOption by remember { mutableStateOf(selectedType) }

    Column {
        exerciseTypeOptions.forEach { typeName ->
            val type = ExerciseType.valueOf(typeName)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (type == selectedOption),
                        onClick = {
                            selectedOption = type
                            onTypeSelected(type)
                        }
                    )
            ) {
                RadioButton(
                    selected = (type == selectedOption),
                    onClick = {
                        selectedOption = type
                        onTypeSelected(type)
                    }
                )
                Text(
                    text = typeName.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
