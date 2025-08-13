package com.example.chatroom.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroom.data.Message
import com.example.chatroom.data.Result
import com.example.chatroom.data.Room
import com.example.chatroom.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> get() = _rooms


    private val roomRepository: RoomRepository = RoomRepository(Injection.instance())

    init {
        loadRooms()
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            roomRepository.createRoom(name)
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            roomRepository.getRooms()
                .catch { exception ->
                    // Handle any errors from Firestore here
                    Log.e("RoomViewModel", "Error listening for room updates", exception)
                    // You could set an error state for the UI here
                }
                .collect { roomList ->
                    // This will be called every time the list of rooms changes
                    _rooms.value = roomList
                }
        }
    }
}