package com.swolebro.view

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.swolebro.R
import com.swolebro.viewmodel.UserViewModel

class GoogleSignInHelper(private val activity: ComponentActivity, launcher: ActivityResultLauncher<Intent>, userViewModel: UserViewModel) {

    private val signInLauncher = launcher

    private val gsiClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(activity, gso)
    }

    fun signInWithGoogle() {
            val signInIntent = gsiClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
}