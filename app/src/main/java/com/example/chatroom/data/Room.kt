package com.example.chatroom.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Room(
    val id : String = "",
    val name : String = ""
)


