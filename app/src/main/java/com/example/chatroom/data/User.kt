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

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Boolean> =
    try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(email,firstName,lastName)
            authResult.user?.sendEmailVerification()?.await()
            saveUserToFirestore(user)
            Result.Success(true)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }

    suspend fun getCurrentUser(): Result<User> = try {
        val uid = auth.currentUser?.email
        if (uid != null) {
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                Log.d("user2","$uid")
                Result.Success(user)
            } else {
                Result.Error(Exception("User data not found"))
            }
        } else {
            Result.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}
