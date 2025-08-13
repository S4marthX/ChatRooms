package com.example.chatroom.data

data class Message(
    val text: String = "",
    val senderFirstName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByCurrentUser: Boolean = false,
    val senderId : String = ""
)