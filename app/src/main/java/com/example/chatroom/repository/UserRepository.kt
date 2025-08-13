package com.example.chatroom.repository

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chatroom.data.User
import com.example.chatroom.viewModel.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth : FirebaseAuth,
    private val firestore: FirebaseFirestore
) {


    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): AuthState {
        return try {
            // Step 1: Create the user in Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                // Step 2: Create the user profile object
                val user = User(
                    firstName = firstName,
                    lastName = lastName,
                    email = email
                )

                // Step 3: Save the user profile to Firestore
                firestore.collection("users").document(userId).set(user).await()
                Log.d("UserRepository", "Successfully created user document in Firestore for UID: $userId")

                // Step 4: Send the verification email
                sendEmailVerification() // Assuming this is another function in your repo
                AuthState.VerificationSent("Verification email sent.")
            } else {
                AuthState.Error("Sign up successful, but failed to get user ID.")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error during sign up.", e)
            AuthState.Error(e.message ?: "An unknown error occurred during sign up.")
        }
    }

    suspend fun login(email: String, password: String) : AuthState {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && user.isEmailVerified) {
                AuthState.Success("Login successful.")
            } else {
                sendEmailVerification()
                // Optionally resend verification email upon login attempt if not verified
            }
        } catch (e: Exception) {
            AuthState.Error(e.message ?: "An unknown error occurred.")
        }
    }

    suspend fun sendEmailVerification(): AuthState {
        val user = firebaseAuth.currentUser

        // Check if the user exists first
        if (user == null) {
            return AuthState.Error("No user is signed in to send verification email.")
        }

        // Try to send the email and return the result
        return try {
            user.sendEmailVerification().await()
            AuthState.VerificationSent("Verification email sent to ${user.email}")
        } catch (e: Exception) {
            AuthState.Error("Failed to send verification email: ${e.message}")
        }
    }

    suspend fun checkEmailVerification() : AuthState{
        val user = firebaseAuth.currentUser
        if (user != null) {
             return try {
                user.reload().await()
                // Re-check the value from the currentUser StateFlow which is updated by the AuthStateListener
                if (firebaseAuth.currentUser?.isEmailVerified == true) {
                    AuthState.Success("Email verified successfully!")
                } else {
                     AuthState.Error("Email is still not verified.")
                }
            } catch (e: Exception) {
                AuthState.Error("Failed to check verification status: ${e.message}")
            }
        }
        else{
            return AuthState.Error("No user is signed in to check verification status.")
        }
    }

    suspend fun getCurrentUserFromFirestore(): User? {
        val userId = firebaseAuth.currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject<User>()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user from Firestore.", e)
            null
        }
    }
}