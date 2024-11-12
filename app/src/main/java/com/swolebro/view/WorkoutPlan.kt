package com.swolebro.view

import CustomButton
import CustomOutlinedTextFieldWithError
import EditableTextFieldWithError
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WorkoutPlan(navController: NavHostController, viewModel: UserViewModel, workoutViewModel: WorkoutViewModel) {
    var newHeight by remember{ mutableStateOf("") }
    var newWeight by remember{ mutableStateOf("") }
    var heightError by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf("") }
    val userMeasurements by viewModel.userMeasurements.collectAsState()
    val isCalculatingBmi by viewModel.isCalculatingBmi.collectAsState()
    val bmi by viewModel.bmi.collectAsState("0")
    val bmiCat by viewModel.bmiCat.collectAsState("")

    fun heightValidator(height: String): String {
        return if (!height.matches("^\\d*\\.?\\d*$".toRegex())) "Height must be a number" else ""
    }

    fun weightValidator(weight: String): String {
        return if (!weight.matches("^\\d*\\.?\\d*$".toRegex())) "Weight must be a number" else ""
    }

    viewModel.getDefaultUnits()
    viewModel.getHeightWeightBmi()
    LaunchedEffect(userMeasurements){
        viewModel.calculateBMI()
    }
    LaunchedEffect(bmi){
        viewModel.getBMICategory(bmi)
    }

    Column(
        modifier = Modifier.padding(24.dp, 128.dp, 24.dp, 24.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var validatedHeight by remember { mutableStateOf(false) }
        var validatedWeight by remember { mutableStateOf(false) }
        if (!validatedWeight or !validatedHeight) {
            Text(
                "Please input your height and weight",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomOutlinedTextFieldWithError(
                value = newHeight,
                label = "Height (m)",
                onValueChange = {
                    newHeight = it
                    heightError = heightValidator(newHeight)
                },
                error = heightError
            )
            CustomOutlinedTextFieldWithError(
                value = newWeight,
                label = "Weight (kg)",
                onValueChange = {
                    newWeight = it
                    weightError = weightValidator(newWeight)
                },
                error = weightError
            )
            CustomButton(
                onClick = {
                    if (heightError.isEmpty()) {
                        viewModel.updateHeight(newHeight)
                        validatedHeight = true
                    }
                    if (weightError.isEmpty()) {
                        viewModel.updateWeight(newWeight)
                        validatedWeight = true
                    }
                },
                text = "Submit"
            )
        } else {
            if (isCalculatingBmi) {
                CircularProgressIndicator()
            } else {
                BmiSlider(bmi, bmiCat)
                when (bmiCat) {
                    "Under Weight" -> {
                        Text("We recommend bulking based on your BMI!")
                    }

                    "Over Weight", "Obese" -> {
                        Text("We recommend cutting based on your BMI!")
                    }

                    "Normal Weight" -> {
                        Text("We recommend cutting and bulking based on your BMI!")
                    }

                    else -> {
                        //
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        when (bmiCat) {
                            "Under Weight" -> {
                                workoutViewModel.createDefaultBulkTemplate()
                            }
                            "Over Weight", "Obese" -> {
                                workoutViewModel.createDefaultCutTemplate()
                            }
                            "Normal Weight" -> {
                                workoutViewModel.createDefaultBulkTemplate()
                                workoutViewModel.createDefaultCutTemplate()
                            }
                        }
                        navController.navigate("workout")
                    },
                    shape = RoundedCornerShape(16.dp),
                    enabled = (validatedHeight && validatedWeight)
                ) {
                    Text("Let's get started!")
                }
            }
        }
    }
}
