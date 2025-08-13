package com.example.chatroom.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroom.data.Message
import com.example.chatroom.repository.MessageRepository
import com.example.chatroom.data.Result
import com.example.chatroom.data.User
import com.example.chatroom.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessageViewModel : ViewModel() {

    private val messageRepository: MessageRepository = MessageRepository(Injection.instance())
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val userRepository : UserRepository = UserRepository(
        firebaseAuth,
        Injection.instance()
    )

    // --- StateFlow Declarations ---
    // StateFlow is generally preferred over LiveData for Jetpack Compose apps.
    // It requires an initial value, which makes state management more predictable.

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages

    private val _roomId = MutableStateFlow<String?>(null)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    init {
        // Load the user immediately. This is synchronous and doesn't need a coroutine.
        loadCurrentUser()
    }

    fun setRoomId(roomId: String) {
        _roomId.value = roomId
        loadMessages()
    }

    fun loadMessages() {
        // Ensure roomId is not null before proceeding
        val currentRoomId = _roomId.value ?: return

        viewModelScope.launch {
            messageRepository.getChatMessages(currentRoomId)
                .collect { messageList ->
                    _messages.value = messageList
                }
        }
    }

    fun sendMessage(text: String) {
        // Ensure both roomId and the user are available before sending
        val currentRoomId = _roomId.value ?: return
        val user = _currentUser.value ?: return

        val message = Message(
            senderFirstName = user.firstName,
            senderId = user.email,
            text = text
        )

        viewModelScope.launch {
            // You can handle the result here if you need to show an error, etc.
            messageRepository.sendMessage(currentRoomId, message)
        }
    }

    private fun loadCurrentUser() {
        // Launch a coroutine to fetch the detailed user profile
        viewModelScope.launch {
            // Call your repository to get the full user object from Firestore
            val userProfile = userRepository.getCurrentUserFromFirestore()
            _currentUser.value = userProfile
        }
    }
}