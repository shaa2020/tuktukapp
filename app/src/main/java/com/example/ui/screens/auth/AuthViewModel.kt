package com.example.ui.screens.auth

import android.content.Context
import android.app.Activity
import com.example.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val userProfile: UserProfile, val isGuest: Boolean) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userRepository.userProfile

    val isAuthenticated: StateFlow<Boolean> = userRepository.userProfile
        .map { !it.isGuest }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = !userRepository.userProfile.value.isGuest
        )

    val isGuest: StateFlow<Boolean> = userRepository.userProfile
        .map { it.isGuest }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = userRepository.userProfile.value.isGuest
        )

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun isUserLoggedIn(): Boolean {
        return !userRepository.userProfile.value.isGuest
    }

    fun loginWithGoogle(
        context: Context,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val webClientId = try { BuildConfig.GOOGLE_WEB_CLIENT_ID } catch (e: Exception) { "" }
                val result = userRepository.firebaseAuthService.signInWithGoogle(context, webClientId)
                if (result.isSuccess) {
                    val name = result.displayName ?: "Carlos Silva"
                    val email = result.userEmail ?: "carlos.silva.tuk@gmail.com"
                    userRepository.loginWithGoogle(name, email)
                    val profile = userRepository.userProfile.value
                    _uiState.value = AuthUiState.Success(profile, isGuest = false)
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error(result.errorMessage ?: "Google sign in failed")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Google sign in failed")
            }
        }
    }

    fun loginWithApple(
        activity: Activity?,
        onSuccess: () -> Unit = {}
    ) {
        if (activity == null) {
            _uiState.value = AuthUiState.Error("Unable to find host Activity for Apple Sign-In")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            userRepository.firebaseAuthService.signInWithApple(activity) { result ->
                if (result.isSuccess) {
                    val name = result.displayName ?: "Helena Santos"
                    val email = result.userEmail ?: "helena.santos@icloud.com"
                    userRepository.loginWithApple(name, email)
                    val profile = userRepository.userProfile.value
                    _uiState.value = AuthUiState.Success(profile, isGuest = false)
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error(result.errorMessage ?: "Apple sign in failed")
                }
            }
        }
    }

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = userRepository.firebaseAuthService.signInWithEmailAndPassword(email, password)
                if (result.isSuccess) {
                    val finalName = result.displayName ?: email.substringBefore("@").replace(".", " ")
                    val finalEmail = result.userEmail ?: email
                    userRepository.loginWithEmail(finalEmail, finalName)
                    val profile = userRepository.userProfile.value
                    _uiState.value = AuthUiState.Success(profile, isGuest = false)
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error(result.errorMessage ?: "Email sign in failed")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Email sign in failed")
            }
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit = {}
    ) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }
        if (name.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your full name.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = userRepository.firebaseAuthService.signUpWithEmailAndPassword(email, password, name)
                if (result.isSuccess) {
                    val finalEmail = result.userEmail ?: email
                    val finalName = result.displayName ?: name
                    userRepository.loginWithEmail(finalEmail, finalName)
                    val profile = userRepository.userProfile.value
                    _uiState.value = AuthUiState.Success(profile, isGuest = false)
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error(result.errorMessage ?: "Registration failed")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun sendPasswordReset(
        email: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (email.isBlank()) {
            onComplete(false, "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            val result = userRepository.firebaseAuthService.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                onComplete(true, null)
            } else {
                onComplete(false, result.errorMessage)
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.continueAsGuest()
            val profile = userRepository.userProfile.value
            _uiState.value = AuthUiState.Success(profile, isGuest = true)
            onSuccess()
        }
    }

    fun loginAsDemoTraveler(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            userRepository.loginWithEmail("alex.rivera@example.com", "Alex Rivera")
            val profile = userRepository.userProfile.value
            _uiState.value = AuthUiState.Success(profile, isGuest = false)
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
