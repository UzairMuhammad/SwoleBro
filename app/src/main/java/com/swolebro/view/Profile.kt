package com.swolebro.view

import CustomOutlinedTextField
import CustomOutlinedTextFieldWithError
import EditableTextFieldWithError
import TopBar
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Profile(viewModel: UserViewModel, workoutViewModel: WorkoutViewModel) {
    val name by viewModel.name.observeAsState("")
    var newName by remember { mutableStateOf("")}

    var nameError by remember { mutableStateOf("") }

    val defaultUnits by viewModel.defaultUnits.observeAsState("")
    val bmi by viewModel.bmi.collectAsState("0")
    val bmiCat by viewModel.bmiCat.collectAsState("")
    val userMeasurements by viewModel.userMeasurements.collectAsState()
    var newHeight by remember{ mutableStateOf("") }
    var newWeight by remember{ mutableStateOf("") }
    val isCalculatingBmi by viewModel.isCalculatingBmi.collectAsState()
    var heightError by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf("") }

    val streak by workoutViewModel.streak.observeAsState("")
    val streakGoal by viewModel.streakGoal.observeAsState("")

    fun heightValidator(height: String): String {
        return if (!userMeasurements.height.matches("^\\d*\\.?\\d*$".toRegex())) "Height must be a number" else ""
    }

    fun weightValidator(weight: String): String {
        return if (!userMeasurements.height.matches("^\\d*\\.?\\d*$".toRegex())) "Weight must be a number" else ""
    }

    LaunchedEffect(name){
        viewModel.getName()
        newName = name
        newHeight = userMeasurements.height
        newWeight = userMeasurements.weight
    }

    viewModel.getDefaultUnits()
    viewModel.getHeightWeightBmi()
    LaunchedEffect(userMeasurements){
        viewModel.calculateBMI()
    }
    LaunchedEffect(bmi){
        viewModel.getBMICategory(bmi)
    }

    var updatedStreak by remember { mutableIntStateOf(-1) }
    LaunchedEffect(streak){
        if (streak.isEmpty()) {
            workoutViewModel.getStreak()
        } else {
            workoutViewModel.getStreak()
            updatedStreak = streak.toInt()
        }
    }

    var newStreakGoal by remember { mutableIntStateOf(-1) }
    var updatedStreakGoal by remember { mutableIntStateOf(-1) }
    LaunchedEffect(streakGoal){
        if (streakGoal.isEmpty()) {
            viewModel.getStreakGoal()
        } else {
            viewModel.getStreakGoal()
            newStreakGoal = streakGoal.toInt()
            updatedStreakGoal = streakGoal.toInt()
        }
    }

    LazyColumn(
        modifier = Modifier.padding(24.dp)
    ) {
        items(count = 1) {
            Text("Profile Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditableTextFieldWithError(
                    value = newName,
                    label = "Name",
                    onValueChange = {
                        newName = it
                        nameError = nameValidator(newName)
                    },
                    onClick = {
                        if (nameError.isEmpty()) {
                            viewModel.updateName(newName)
                        }
                    },
                    error = nameError
                )
                var expanded by remember { mutableStateOf(false) }
                val unitOptions = arrayOf("kg", "lbs")
                var selectedText by remember { mutableStateOf(defaultUnits) }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        CustomOutlinedTextField(
                            value = defaultUnits,
                            label = "Workout Units",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(),
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            unitOptions.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(text = unit) },
                                    onClick = {
                                        selectedText = unit
                                        viewModel.updateUnits(selectedText)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("BMI", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditableTextFieldWithError(
                    value = newHeight,
                    label = "Height (m)",
                    onValueChange = {
                        newHeight = it
                        heightError = heightValidator(newHeight)
                    },
                    onClick = {
                        if (heightError.isEmpty()) {
                            viewModel.updateHeight(newHeight)
                        }
                    },
                    error = heightError
                )
                EditableTextFieldWithError(
                    value = newWeight,
                    label = "Weight (kg)",
                    onValueChange = {
                        newWeight = it
                        weightError = weightValidator(newWeight)
                    },
                    onClick = {
                        if (weightError.isEmpty()) {
                            viewModel.updateWeight(newWeight)
                        }
                    },
                    error = weightError
                )

                if (isCalculatingBmi) {
                    CircularProgressIndicator()
                } else {
                    BmiSlider(bmi, bmiCat)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Streak", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text("Your Current Streak is: " + "$updatedStreak")
                Text("Your Current Streak Goal is: " + "$updatedStreakGoal")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Change Current Goal: ", style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = { newStreakGoal += -1 }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text("$newStreakGoal", textAlign = TextAlign.Center, modifier = Modifier.width(IntrinsicSize.Min))
                    IconButton(onClick = { newStreakGoal += 1 }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
                Button(
                    onClick = {
                        viewModel.postNewStreakGoal(newStreakGoal)
                        updatedStreakGoal = newStreakGoal
                        viewModel.getStreakGoal()
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Update Streak Goal")
                }
            }
        }
    }
}

@Composable
fun EditableTextField(fieldName: String, fieldVal: String, onChange: (String) -> Unit){
    var editMode by remember { mutableStateOf(false) }
    var tempVal by remember(fieldVal) { mutableStateOf(fieldVal) }
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (editMode) {
            TextField(
                value = tempVal,
                onValueChange = { tempVal = it },
                label = { Text(fieldName) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }
        else{
            TextField(
                value = tempVal,
                onValueChange = {},
                label = { Text(fieldName) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.width(18.dp))
        IconButton(onClick = {
            if (editMode) {
                if (tempVal != fieldVal)
                {onChange(tempVal)}
            }
            editMode = !editMode
        }) {
            Icon(
                imageVector = if (editMode) Icons.Filled.Check else Icons.Filled.Edit,
                contentDescription = "Edit $fieldName",
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}


@Composable
fun BmiSlider(bmi: String, weightCategory: String){
    val bmiFloat = if (bmi == "") {0f} else {bmi.toFloat()}
    val bmiRange = 18.5f..30f
    Log.d("BMI", "bmiFloat: $bmiFloat, weightCat: $weightCategory")

    Column{
        Text(
            text =
            if (bmiFloat == 0f){
                "Set your height and weight to calculate BMI"
            }
            else{
                "BMI: $bmiFloat: $weightCategory"
            },
            style = MaterialTheme.typography.labelMedium
        )
        Slider(
            value = bmiFloat,
            onValueChange = {},
            enabled = false,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 2,
            valueRange = bmiRange
        )
    }
}
