package com.example.chatroom.view


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chatroom.screen.Screen
import com.example.chatroom.viewModel.AuthState
import com.example.chatroom.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            is AuthState.Success -> {
                // This will trigger navigation via AppNavigator
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AuthState.VerificationSent -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verify Your Email") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "A verification email has been sent to ${currentUser?.email}. Please check your inbox and click the link to verify your account.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        authViewModel.checkEmailVerification()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I've Verified My Email")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        authViewModel.sendEmailVerification()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Resend Verification Email")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = {
                authViewModel.signOut()
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo("verification") { inclusive = true }
                }
            }) {
                Text("Back to Login")
            }
        }
    }
}
