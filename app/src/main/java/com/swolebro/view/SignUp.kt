package com.swolebro.view

import CustomButton
import CustomOutlinedTextFieldWithError
import CustomPasswordField
import SignInSwoleBroHeader
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.swolebro.viewmodel.UserViewModel
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
    fun SignUpScreen(
    viewModel: UserViewModel,
    navController: NavHostController
) {
    var formSubmitted by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(32.dp),
    ) {
        SignInSwoleBroHeader()
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            CustomOutlinedTextFieldWithError(
                value = name,
                onValueChange = {
                    name = it
                    nameError = nameValidator(name)
                },
                label = "Name",
                error = nameError
            )
            CustomOutlinedTextFieldWithError(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = nameValidator(username)
                },
                label = "Username",
                error = usernameError
            )
            CustomOutlinedTextFieldWithError(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (emailError.isNotEmpty()) emailValidator(email) else ""
                },
                label = "Email",
                error = emailError
            )
            CustomPasswordField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = passwordValidator(formSubmitted, password)
                },
                label = "Password",
                error = passwordError
            )
            CustomButton(
                onClick = {
                    formSubmitted = true
                    nameError = nameValidator(name)
                    usernameError = nameValidator(username)
                    emailError = emailValidator(email)
                    passwordError = passwordValidator(formSubmitted, password)
                    if (nameError.isBlank() && emailError.isBlank() && passwordError.isBlank()) {
                        println("Going to signup")
                        viewModel.signUp(email, password, name) { signupResult ->
                            signupResult.onSuccess { navController.navigate("workout_plan") }
                            signupResult.onFailure { Log.d("Sign-up", "Issue upon Sign-up attempt") }
                        }
                    } else {
                        println("missing fields")
                    }
                },
                text = "Sign Up"
            )
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)) {
                        append("Already have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Login.")
                    }
                },
                modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)
            ) { _ ->
                navController.navigate("login")
            }
        }
    }
}


