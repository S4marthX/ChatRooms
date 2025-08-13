package com.example.chatroom

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatroom.screen.Screen
import com.example.chatroom.view.ChatRoomListScreen
import com.example.chatroom.view.ChatScreen
import com.example.chatroom.view.EmailVerificationScreen
import com.example.chatroom.view.LoginScreen
import com.example.chatroom.view.SignUpScreen
import com.example.chatroom.viewModel.AuthState
import com.example.chatroom.viewModel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigator(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Determine the start destination based on the current user's state
    // If the user is logged in and their email is verified, go to home, otherwise login.
    val startDestination = if (currentUser != null && currentUser!!.isEmailVerified) Screen.ChatroomScreen.route else Screen.LoginScreen.route

    // Listen for auth state changes to navigate automatically
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                // On successful login/verification, navigate to the home screen
                navController.navigate(Screen.ChatroomScreen.route) {
                    // Clear the back stack up to the login screen to prevent going back
                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    // Also clear signup and verification from the back stack if they exist
                    popUpTo(Screen.SignupScreen.route) { inclusive = true }
                    popUpTo(Screen.VerifyEmailScreen.route) { inclusive = true }
                }
                authViewModel.resetAuthState()
            }
            is AuthState.VerificationSent -> {
                // After signing up, a verification email is sent. Navigate to the verification screen.
                navController.navigate(Screen.VerifyEmailScreen.route) {
                    // Clear the signup screen from the back stack
                    popUpTo(Screen.SignupScreen.route) { inclusive = true }
                }
                // Show a toast message to the user
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            // Error state is handled within each screen, so no navigation logic needed here
            else -> {}
        }
    }

    // Setup the navigation host with all the possible screens
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.SignupScreen.route) {
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.VerifyEmailScreen.route) {
            EmailVerificationScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.ChatroomScreen.route) {
            ChatRoomListScreen(
                onJoinClicked = {navController.navigate("${Screen.ChatsScreen.route}/${it.id}")},
                navController = navController
            )

        }
        composable("${Screen.ChatsScreen.route}/{roomId}") {
            val roomId: String = it
                .arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }
    }
}