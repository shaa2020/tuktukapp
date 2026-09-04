package com.example.data.model

enum class UserRole {
    CUSTOMER,
    GUIDE,
    PARTNER,
    MANAGER,
    ADMIN,
    SUPER_ADMIN
}

enum class AuthProvider(val displayName: String) {
    GUEST("Guest"),
    GOOGLE("Google Account"),
    APPLE("Apple ID"),
    EMAIL("Email")
}

data class SavedTraveler(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val passportOrId: String? = null
)

data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val preferredLanguage: String = "English",
    val preferredCurrency: String = "EUR",
    val role: UserRole = UserRole.CUSTOMER,
    val savedTravelers: List<SavedTraveler> = emptyList(),
    val isGuest: Boolean = true,
    val authProvider: AuthProvider = AuthProvider.GUEST,
    val avatarUrl: String? = null,
    val co2SavedKg: Double = 4.8,
    val toursCompletedCount: Int = 2,
    val loyaltyTier: String = "Eco Explorer",
    val notificationsEnabled: Boolean = true,
    val offlineGuidesEnabled: Boolean = true
)

