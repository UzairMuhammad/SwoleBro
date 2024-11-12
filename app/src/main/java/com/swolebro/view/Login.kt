package com.swolebro.view

import CustomButton
import CustomOutlinedTextField
import CustomOutlinedTextFieldWithError
import CustomPasswordField
import SignInGoogleButton
import SignInSwoleBroHeader
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.swolebro.viewmodel.UserViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(viewModel: UserViewModel, navController: NavController, gsiHelper: GoogleSignInHelper) {
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(32.dp),
    ) {
        SignInSwoleBroHeader()
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var emailError by remember { mutableStateOf("") }
            var passwordError by remember { mutableStateOf("") }
            var loginError by remember { mutableStateOf("") }
            fun emailValidator(): String {
                return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) "Invalid email address" else ""
            }

            fun passwordValidator(): String {
                return if (password.isBlank()) "Please type a password" else ""
            }

            Spacer(modifier = Modifier.height(32.dp))
            CustomOutlinedTextFieldWithError(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (emailError.isNotEmpty()) emailValidator() else ""
                },
                label = "Email",
                error = emailError
            )
            CustomPasswordField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (passwordError.isNotEmpty()) passwordValidator() else ""
                },
                label = "Password",
                error = passwordError
            )
            CustomButton(
                onClick = {
                    emailError = emailValidator()
                    passwordError = passwordValidator()
                    if (emailError.isBlank() && passwordError.isBlank()) {
                        println("Going to signup")
                        viewModel.logInNew(email, password) { loginResult ->
                            loginResult.onSuccess { navController.navigate("workout") }
                            loginResult.onFailure { err ->
                                err.message?.let { Log.d("Authentication", it) }
                                if (err.message?.contains("network") == true) {
                                    loginError = "Network error. Please check your connection."
                                } else if (err.message?.contains("auth") == true) {
                                    loginError = "Incorrect email or password. Please double check your credentials."
                                }
                            }
                        }
                        // If error, then show error
                        // passwordError = Error
                    } else {
                        println("missing fields")
                    }
                },
                text = "Login"
            )
            val idToken by viewModel.idToken.observeAsState()
            LaunchedEffect(idToken) {
                idToken?.let {
                    viewModel.logInGoogle { loginResult ->
                        loginResult.onSuccess { navController.navigate("workout") }
                        loginResult.onFailure { err ->
                            err.message?.let { Log.d("Authentication", it) }
                            if (err.message?.contains("network") == true) {
                                loginError = "Network error. Please check your connection."
                            } else if (err.message?.contains("auth") == true) {
                                loginError = "Incorrect email or password. Please double check your credentials."
                            }
                        }
                    }
                }
            }
            SignInGoogleButton(
                onClick={
                    gsiHelper.signInWithGoogle()
                }
            )
            if (loginError.isNotEmpty()) {
                Text(loginError, modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp), color = MaterialTheme.colorScheme.error)
            }
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)) {
                        append("Don't have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Sign Up.")
                    }
                },
                modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)
            ) { _ ->
                navController.navigate("signup")
            }
        }
    }
}


