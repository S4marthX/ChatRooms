package com.example.chatroom.repository

import android.util.Log
import com.example.chatroom.data.Message
import com.example.chatroom.data.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository(private val firestore: FirebaseFirestore) {

    suspend fun sendMessage(roomId: String, message:Message): Result<Unit> = try {
        firestore.collection("rooms").document(roomId)
            .collection("messages").add(message).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    fun getChatMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        // Add a TAG for easy filtering in Logcat
        val TAG = "MessageRepository"

        // FIX #1: Add a guard clause to prevent the crash.
        // If the roomId is empty, we immediately close the flow because the path would be invalid.
        if (roomId.isEmpty()) {
            Log.w(TAG, "getChatMessages called with an empty roomId. Closing flow.")
            close() // Close the flow so it doesn't hang
            return@callbackFlow
        }

        Log.d(TAG, "Setting up message listener for roomId: $roomId")

        val subscription = firestore.collection("rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp") // Good practice to order messages by time
            .addSnapshotListener { querySnapshot, error ->
                // FIX #2: Handle potential errors from Firestore.
                // If there's an error (e.g., permission denied), cancel the flow.
                if (error != null) {
                    Log.e(TAG, "Listen failed for roomId: $roomId", error)
                    cancel("Error fetching messages", error)
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    // FIX #3: Use mapNotNull to safely handle conversion.
                    // This prevents a crash if a message document is ever malformed or null.
                    val messages = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    }
                    Log.d(TAG, "Received ${messages.size} messages for roomId: $roomId")
                    // Send the new list of messages to the collector.
                    trySend(messages)
                } else {
                    Log.d(TAG, "Received a null querySnapshot for roomId: $roomId")
                }
            }

        // This correctly removes the listener when the flow is cancelled.
        awaitClose {
            Log.d(TAG, "Closing message listener for roomId: $roomId")
            subscription.remove()
        }
    }
}