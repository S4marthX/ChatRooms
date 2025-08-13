package com.example.chatroom.repository

import android.system.Os.close
import com.example.chatroom.data.Result
import com.example.chatroom.data.Room
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoomRepository(private val firestore: FirebaseFirestore){

    suspend fun createRoom(name: String): Result<Unit> = try {
        val room = Room(name = name)
        firestore.collection("rooms").add(room).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    fun getRooms(): Flow<List<Room>> = callbackFlow {
        val collection = firestore.collection("rooms")

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                cancel("Error fetching rooms", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Map the documents to Room objects
                val rooms = snapshot.documents.mapNotNull { doc ->
                    // Convert the document to a Room object
                    val room = doc.toObject(Room::class.java)
                    // Manually set the ID from the document snapshot
                    room?.copy(id = doc.id)
                }
                trySend(rooms)
            }
        }
        awaitClose { listener.remove() }
    }
}