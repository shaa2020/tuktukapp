package com.example.data.repository

import com.example.data.model.*
import com.example.data.remote.api.ApiAvailabilityResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class TourSortOption(val displayName: String) {
    RECOMMENDED("Recommended"),
    MOST_POPULAR("Most Popular"),
    HIGHEST_RATED("Highest Rated"),
    PRICE_LOW_HIGH("Lowest Price"),
    PRICE_HIGH_LOW("Highest Price"),
    SHORTEST_DURATION("Shortest Duration")
}

data class FilterParams(
    val query: String = "",
    val destination: String? = null,
    val categoryId: String? = null,
    val maxPriceEur: Double = 300.0,
    val minRating: Double = 0.0,
    val experienceType: ExperienceType? = null,
    val language: String? = null,
    val pickupOnly: Boolean = false,
    val instantConfirmationOnly: Boolean = false,
    val sortOption: TourSortOption = TourSortOption.RECOMMENDED
)

class TourRepository(
    val tuktuk24Repository: TukTuk24Repository = TukTuk24Repository()
) {
    val toursState: StateFlow<List<Tour>> = tuktuk24Repository.toursState

    init {
        // Asynchronously sync with live TukTuk24 backend API
        CoroutineScope(Dispatchers.IO).launch {
            tuktuk24Repository.fetchLiveTours()
        }
    }

    suspend fun refreshTours(): Result<List<Tour>> {
        return tuktuk24Repository.fetchLiveTours()
    }

    fun getTourById(id: String): Tour? {
        return toursState.value.find { it.id.equals(id, ignoreCase = true) || it.title.contains(id, ignoreCase = true) }
    }

    suspend fun fetchTourByIdLive(id: String): Tour? {
        return tuktuk24Repository.getTourById(id)
    }

    suspend fun checkAvailability(
        tourId: String,
        date: String,
        time: String? = null,
        partySize: Int = 2
    ): ApiAvailabilityResponse {
        return tuktuk24Repository.checkAvailability(tourId, date, time, partySize)
    }

    fun getFilteredTours(params: FilterParams): List<Tour> {
        val list = toursState.value.filter { tour ->
            // Query search
            val matchesQuery = params.query.isBlank() ||
                    tour.title.contains(params.query, ignoreCase = true) ||
                    tour.destination.contains(params.query, ignoreCase = true) ||
                    tour.description.contains(params.query, ignoreCase = true) ||
                    tour.category.displayName.contains(params.query, ignoreCase = true)

            // Destination filter
            val matchesDestination = params.destination.isNullOrBlank() ||
                    params.destination.equals("All", ignoreCase = true) ||
                    tour.destination.contains(params.destination!!, ignoreCase = true)

            // Category filter
            val matchesCategory = params.categoryId.isNullOrBlank() ||
                    tour.category.id.equals(params.categoryId, ignoreCase = true)

            // Price filter
            val matchesPrice = tour.basePriceEur <= params.maxPriceEur

            // Rating filter
            val matchesRating = tour.rating >= params.minRating

            // Experience type
            val matchesType = params.experienceType == null || tour.experienceType == params.experienceType

            // Language
            val matchesLang = params.language.isNullOrBlank() ||
                    tour.languages.any { it.contains(params.language!!, ignoreCase = true) }

            // Pickup
            val matchesPickup = !params.pickupOnly || tour.pickupAvailable

            // Instant confirmation
            val matchesInstant = !params.instantConfirmationOnly || tour.instantConfirmation

            matchesQuery && matchesDestination && matchesCategory && matchesPrice &&
                    matchesRating && matchesType && matchesLang && matchesPickup && matchesInstant
        }

        return when (params.sortOption) {
            TourSortOption.RECOMMENDED -> list.sortedByDescending { if (it.isRecommended) 1 else 0 }
            TourSortOption.MOST_POPULAR -> list.sortedByDescending { it.reviewCount }
            TourSortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
            TourSortOption.PRICE_LOW_HIGH -> list.sortedBy { it.basePriceEur }
            TourSortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.basePriceEur }
            TourSortOption.SHORTEST_DURATION -> list.sortedBy { it.durationHours }
        }
    }

    fun getFeaturedTours(): List<Tour> = toursState.value.filter { it.isFeatured }
    fun getPopularTours(): List<Tour> = toursState.value.filter { it.isPopular }
    fun getRecommendedTours(): List<Tour> = toursState.value.filter { it.isRecommended }
    fun getLastMinuteTours(): List<Tour> = toursState.value.filter { it.isLastMinute }
}
