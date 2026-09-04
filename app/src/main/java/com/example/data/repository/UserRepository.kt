package com.example.data.repository

import com.example.data.model.*
import com.example.domain.auth.FirebaseAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(
    val firebaseAuthService: FirebaseAuthService = FirebaseAuthService()
) {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "user_guest_101",
            fullName = "Alex Rivera",
            email = "alex.rivera@example.com",
            phone = "+351 910 000 000",
            preferredLanguage = "English",
            preferredCurrency = "EUR",
            role = UserRole.CUSTOMER,
            savedTravelers = listOf(
                SavedTraveler("trv_1", "Alex Rivera", "alex.rivera@example.com", "+351 910 000 000"),
                SavedTraveler("trv_2", "Sophia Rivera", "sophia.r@example.com", "+351 910 111 222")
            ),
            isGuest = false,
            authProvider = AuthProvider.GOOGLE
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _currentCurrency = MutableStateFlow(AppCurrency.EUR)
    val currentCurrency: StateFlow<AppCurrency> = _currentCurrency.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun updateCurrency(currency: AppCurrency) {
        _currentCurrency.value = currency
        _userProfile.value = _userProfile.value.copy(preferredCurrency = currency.code)
    }

    fun updateLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        _userProfile.value = _userProfile.value.copy(preferredLanguage = language.displayName)
    }

    fun loginWithGoogle(
        name: String = "Carlos Silva",
        email: String = "carlos.silva.tuk@gmail.com"
    ) {
        _userProfile.value = _userProfile.value.copy(
            id = "usr_google_778",
            fullName = name,
            email = email,
            phone = "+351 912 345 678",
            isGuest = false,
            authProvider = AuthProvider.GOOGLE
        )
    }

    fun loginWithApple(
        name: String = "Helena Santos",
        email: String = "helena.santos@icloud.com"
    ) {
        _userProfile.value = _userProfile.value.copy(
            id = "usr_apple_992",
            fullName = name,
            email = email,
            phone = "+351 934 567 890",
            isGuest = false,
            authProvider = AuthProvider.APPLE
        )
    }

    fun loginWithEmail(
        email: String,
        name: String = "Lisbon Explorer"
    ) {
        _userProfile.value = _userProfile.value.copy(
            id = "usr_email_" + email.hashCode().toString().take(6),
            fullName = if (name.isNotBlank()) name else email.substringBefore("@").replace(".", " ").capitalize(),
            email = email,
            phone = "+351 900 123 456",
            isGuest = false,
            authProvider = AuthProvider.EMAIL
        )
    }

    fun continueAsGuest() {
        _userProfile.value = _userProfile.value.copy(
            id = "usr_guest_" + System.currentTimeMillis().toString().takeLast(6),
            fullName = "Guest Traveler",
            email = "",
            phone = "",
            isGuest = true,
            authProvider = AuthProvider.GUEST
        )
    }

    fun logout() {
        firebaseAuthService.signOut()
        _userProfile.value = _userProfile.value.copy(
            fullName = "Guest Traveler",
            email = "",
            phone = "",
            isGuest = true,
            authProvider = AuthProvider.GUEST
        )
    }

    fun updateProfile(name: String, email: String, phone: String) {
        _userProfile.value = _userProfile.value.copy(
            fullName = name,
            email = email,
            phone = phone,
            isGuest = false
        )
    }

    fun addSavedTraveler(traveler: SavedTraveler) {
        val currentList = _userProfile.value.savedTravelers.toMutableList()
        currentList.add(traveler)
        _userProfile.value = _userProfile.value.copy(savedTravelers = currentList)
    }

    fun deleteSavedTraveler(travelerId: String) {
        val updated = _userProfile.value.savedTravelers.filterNot { it.id == travelerId }
        _userProfile.value = _userProfile.value.copy(savedTravelers = updated)
    }

    fun updateSavedTraveler(updatedTraveler: SavedTraveler) {
        val updated = _userProfile.value.savedTravelers.map {
            if (it.id == updatedTraveler.id) updatedTraveler else it
        }
        _userProfile.value = _userProfile.value.copy(savedTravelers = updated)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(notificationsEnabled = enabled)
    }

    fun setOfflineGuidesEnabled(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(offlineGuidesEnabled = enabled)
    }
}
