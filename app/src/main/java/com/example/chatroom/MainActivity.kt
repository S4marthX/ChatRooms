package com.example.chatroom

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatroom.screen.Screen
import com.example.chatroom.ui.theme.ChatRoomTheme
import com.example.chatroom.view.ChatRoomListScreen
import com.example.chatroom.view.ChatScreen
import com.example.chatroom.view.LoginScreen
import com.example.chatroom.view.SignUpScreen
import com.example.chatroom.viewModel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatRoomTheme {
                AppNavigator()
            }
        }
    }
}

