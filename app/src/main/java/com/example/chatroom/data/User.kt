package com.example.chatroom.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

data class User(
    val email : String = "",
    val firstName : String = "",
    val lastName : String = ""
)
