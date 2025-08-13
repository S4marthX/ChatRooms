package com.example.chatroom.screen

sealed class Screen(val route : String) {
    object LoginScreen : Screen("login_screen")
    object SignupScreen : Screen("signup_screen")
    object ChatroomScreen:Screen("chatroom_screen")
    object ChatsScreen:Screen("chats_screen")
    object VerifyEmailScreen:Screen("verify_email_screen")
}