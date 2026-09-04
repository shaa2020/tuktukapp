package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TourCategory(val id: String, val displayName: String, val iconName: String) {
    HISTORIC("historic", "Historic & Cultural", "Castle"),
    SIGHTSEEING("sightseeing", "Sightseeing Highlights", "Compass"),
    COASTAL("coastal", "Coastal & Beaches", "BeachAccess"),
    SUNSET("sunset", "Sunset & Night", "WbSunny"),
    FOOD_WINE("food_wine", "Food & Wine Tasting", "Restaurant"),
    DAY_TRIP("day_trip", "Full Day Day-Trips", "DirectionsCar"),
    FAMILY("family", "Family Friendly", "FamilyRestroom"),
    COUPLES("couples", "Romantic & Couples", "Favorite"),
    FLEET_CONVOY("fleet_convoy", "Group Fleet Convoy", "ElectricRickshaw")
}

enum class ExperienceType {
    PRIVATE,
    SHARED
}

data class TourHighlight(
    val title: String,
    val description: String
)

data class ItineraryStep(
    val stepNumber: Int,
    val title: String,
    val durationMinutes: Int,
    val description: String,
    val locationName: String
)

data class PickupPoint(
    val id: String,
    val name: String,
    val address: String,
    val isHotelPickup: Boolean = true,
    val extraFeeEur: Double = 0.0
)

data class TourExtra(
    val id: String,
    val name: String,
    val description: String,
    val priceEur: Double,
    val iconName: String = "Star"
)

data class GuideInfo(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val languages: List<String>,
    val bio: String,
    val phone: String,
    val whatsapp: String
)

data class TourReview(
    val id: String,
    val userName: String,
    val userCountry: String,
    val userAvatar: String,
    val rating: Int,
    val date: String,
    val title: String,
    val comment: String,
    val isVerifiedBooking: Boolean = true,
    val categoryRatings: Map<String, Int> = mapOf("Guide" to 5, "Vehicle" to 5, "Itinerary" to 5)
)

data class Tour(
    val id: String,
    val title: String,
    val tagline: String,
    val destination: String, // e.g. "Lisbon", "Sintra", "Cascais", "Cabo da Roca", "Fátima", "Nazaré"
    val category: TourCategory,
    val description: String,
    val mainImageUrl: String,
    val galleryImages: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val durationHours: Double,
    val basePriceEur: Double,
    val perGuestPriceEur: Double,
    val experienceType: ExperienceType,
    val languages: List<String>,
    val pickupAvailable: Boolean,
    val freeCancellationHours: Int = 24,
    val instantConfirmation: Boolean = true,
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    val isRecommended: Boolean = false,
    val isLastMinute: Boolean = false,
    val highlights: List<TourHighlight>,
    val itinerary: List<ItineraryStep>,
    val included: List<String>,
    val excluded: List<String>,
    val availableTimeSlots: List<String>,
    val meetingPointAddress: String,
    val pickupPoints: List<PickupPoint>,
    val availableExtras: List<TourExtra>,
    val assignedGuide: GuideInfo? = null,
    val reviews: List<TourReview> = emptyList()
)
