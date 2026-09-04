package com.example.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.data.model.AppCurrency
import com.example.data.model.AppLanguage
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userRepository.userProfile
    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency
    val currentLanguage: StateFlow<AppLanguage> = userRepository.currentLanguage

    fun loginWithGoogle() {
        userRepository.loginWithGoogle()
    }

    fun loginWithApple() {
        userRepository.loginWithApple()
    }

    fun logout() {
        userRepository.logout()
    }

    fun updateProfile(name: String, email: String, phone: String) {
        userRepository.updateProfile(name, email, phone)
    }

    fun updateCurrency(currency: AppCurrency) {
        userRepository.updateCurrency(currency)
    }

    fun updateLanguage(language: AppLanguage) {
        userRepository.updateLanguage(language)
    }

    fun addTraveler(name: String, email: String, phone: String, passportOrId: String? = null) {
        val newTraveler = com.example.data.model.SavedTraveler(
            id = "trv_" + System.currentTimeMillis().toString().takeLast(6),
            fullName = name,
            email = email,
            phone = phone,
            passportOrId = passportOrId
        )
        userRepository.addSavedTraveler(newTraveler)
    }

    fun removeTraveler(travelerId: String) {
        userRepository.deleteSavedTraveler(travelerId)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        userRepository.setNotificationsEnabled(enabled)
    }

    fun setOfflineGuidesEnabled(enabled: Boolean) {
        userRepository.setOfflineGuidesEnabled(enabled)
    }
}
