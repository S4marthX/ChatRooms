package com.example.chatroom.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroom.data.User
import com.example.chatroom.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository = UserRepository(
        FirebaseAuth.getInstance(),
        Injection.instance()
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    fun signUp(email: String, password: String,firstName : String, lastName : String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = userRepository.signUp(email,password,firstName,lastName)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = userRepository.login(email, password)
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = userRepository.sendEmailVerification()

        }
    }

    fun checkEmailVerification() {
        viewModelScope.launch {
            _authState.value = userRepository.checkEmailVerification()
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

// Sealed class to represent different UI states in a type-safe way
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class VerificationSent(val message: String) : AuthState()
}
