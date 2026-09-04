package com.example.data.remote.api

import com.example.data.model.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiTourDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "tagline") val tagline: String? = null,
    @Json(name = "destination") val destination: String = "Lisbon",
    @Json(name = "category") val category: String = "historic",
    @Json(name = "description") val description: String = "",
    @Json(name = "mainImageUrl") val mainImageUrl: String? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "galleryImages") val galleryImages: List<String>? = null,
    @Json(name = "rating") val rating: Double = 5.0,
    @Json(name = "reviewCount") val reviewCount: Int = 0,
    @Json(name = "durationHours") val durationHours: Double = 2.0,
    @Json(name = "basePriceEur") val basePriceEur: Double = 0.0,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "salePrice") val salePrice: Double? = null,
    @Json(name = "currency") val currency: String = "EUR",
    @Json(name = "perGuestPriceEur") val perGuestPriceEur: Double = 15.0,
    @Json(name = "experienceType") val experienceType: String = "PRIVATE",
    @Json(name = "languages") val languages: List<String>? = null,
    @Json(name = "pickupAvailable") val pickupAvailable: Boolean = true,
    @Json(name = "freeCancellationHours") val freeCancellationHours: Int = 24,
    @Json(name = "instantConfirmation") val instantConfirmation: Boolean = true,
    @Json(name = "isActive") val isActive: Boolean = true,
    @Json(name = "isPublished") val isPublished: Boolean = true,
    @Json(name = "isFeatured") val isFeatured: Boolean = false,
    @Json(name = "isPopular") val isPopular: Boolean = false,
    @Json(name = "isRecommended") val isRecommended: Boolean = false,
    @Json(name = "isLastMinute") val isLastMinute: Boolean = false,
    @Json(name = "highlights") val highlights: List<ApiTourHighlightDto>? = null,
    @Json(name = "itinerary") val itinerary: List<ApiItineraryStepDto>? = null,
    @Json(name = "included") val included: List<String>? = null,
    @Json(name = "excluded") val excluded: List<String>? = null,
    @Json(name = "availableTimeSlots") val availableTimeSlots: List<String>? = null,
    @Json(name = "meetingPointAddress") val meetingPointAddress: String? = null,
    @Json(name = "pickupPoints") val pickupPoints: List<ApiPickupPointDto>? = null,
    @Json(name = "availableExtras") val availableExtras: List<ApiTourExtraDto>? = null,
    @Json(name = "assignedGuide") val assignedGuide: ApiGuideDto? = null
)

@JsonClass(generateAdapter = true)
data class ApiTourHighlightDto(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiItineraryStepDto(
    @Json(name = "stepNumber") val stepNumber: Int = 1,
    @Json(name = "title") val title: String,
    @Json(name = "durationMinutes") val durationMinutes: Int = 30,
    @Json(name = "description") val description: String = "",
    @Json(name = "locationName") val locationName: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiPickupPointDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "address") val address: String = "",
    @Json(name = "isHotelPickup") val isHotelPickup: Boolean = true,
    @Json(name = "extraFeeEur") val extraFeeEur: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ApiTourExtraDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String = "",
    @Json(name = "priceEur") val priceEur: Double = 0.0,
    @Json(name = "iconName") val iconName: String = "Star"
)

@JsonClass(generateAdapter = true)
data class ApiGuideDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatarUrl") val avatarUrl: String = "",
    @Json(name = "rating") val rating: Double = 5.0,
    @Json(name = "reviewCount") val reviewCount: Int = 0,
    @Json(name = "languages") val languages: List<String> = emptyList(),
    @Json(name = "bio") val bio: String = "",
    @Json(name = "phone") val phone: String = "+351 912 345 678",
    @Json(name = "whatsapp") val whatsapp: String = "+351912345678"
)

@JsonClass(generateAdapter = true)
data class ApiAvailabilityRequest(
    @Json(name = "tourId") val tourId: String,
    @Json(name = "date") val date: String, // ISO YYYY-MM-DD
    @Json(name = "time") val time: String? = null,
    @Json(name = "partySize") val partySize: Int = 2
)

@JsonClass(generateAdapter = true)
data class ApiAvailabilityResponse(
    @Json(name = "available") val available: Boolean,
    @Json(name = "tourId") val tourId: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "availableSlots") val availableSlots: List<String> = emptyList(),
    @Json(name = "remainingCapacity") val remainingCapacity: Int? = null,
    @Json(name = "pricePerPersonEur") val pricePerPersonEur: Double? = null,
    @Json(name = "totalPriceEur") val totalPriceEur: Double? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiLeadRequest(
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "language") val language: String = "English",
    @Json(name = "partySize") val partySize: Int? = null,
    @Json(name = "preferredDate") val preferredDate: String? = null,
    @Json(name = "preferredTime") val preferredTime: String? = null,
    @Json(name = "tourId") val tourId: String? = null,
    @Json(name = "tourName") val tourName: String? = null,
    @Json(name = "customerMessage") val customerMessage: String = "",
    @Json(name = "source") val source: String = "ai_assistant"
)

@JsonClass(generateAdapter = true)
data class ApiLeadResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "leadId") val leadId: String? = null,
    @Json(name = "message") val message: String = "Lead captured successfully"
)

@JsonClass(generateAdapter = true)
data class ApiBookingRequest(
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "tourId") val tourId: String,
    @Json(name = "date") val date: String, // ISO YYYY-MM-DD
    @Json(name = "time") val time: String,
    @Json(name = "partySize") val partySize: Int = 2,
    @Json(name = "pickupLocation") val pickupLocation: String? = null,
    @Json(name = "selectedLanguage") val selectedLanguage: String = "English",
    @Json(name = "specialRequests") val specialRequests: String? = null,
    @Json(name = "source") val source: String = "ai_assistant"
)

@JsonClass(generateAdapter = true)
data class ApiBookingResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "bookingId") val bookingId: String? = null,
    @Json(name = "status") val status: String = "REQUESTED", // REQUESTED vs CONFIRMED
    @Json(name = "isConfirmed") val isConfirmed: Boolean = false,
    @Json(name = "confirmationCode") val confirmationCode: String? = null,
    @Json(name = "totalAmountEur") val totalAmountEur: Double? = null,
    @Json(name = "paymentUrl") val paymentUrl: String? = null,
    @Json(name = "message") val message: String = "Booking request received"
)

@JsonClass(generateAdapter = true)
data class ApiBusinessInfoDto(
    @Json(name = "companyName") val companyName: String = "TukTuk24 Lisbon",
    @Json(name = "brandName") val brandName: String = "TukTuk24",
    @Json(name = "description") val description: String = "Official 100% Eco-Friendly Electric Tuk-Tuk Tours in Lisbon, Sintra, Cascais & Portugal.",
    @Json(name = "operatingCity") val operatingCity: String = "Lisbon, Portugal",
    @Json(name = "timezone") val timezone: String = "Europe/Lisbon (WEST/WET)",
    @Json(name = "openingHours") val openingHours: String = "08:30 AM - 10:00 PM Daily",
    @Json(name = "phone") val phone: String = "+351 912 345 678",
    @Json(name = "whatsapp") val whatsapp: String = "+351912345678",
    @Json(name = "email") val email: String = "bookings@tuktuk24lisbon.com",
    @Json(name = "cancellationPolicy") val cancellationPolicy: String = "Free cancellation up to 24 hours prior to tour departure.",
    @Json(name = "languagesSupported") val languagesSupported: List<String> = listOf("English", "Portuguese", "Spanish", "French", "German", "Italian", "Dutch", "Bengali", "Arabic"),
    @Json(name = "fleetType") val fleetType: String = "100% Electric Silent Tuk-Tuks (Seats up to 6 passengers per vehicle)",
    @Json(name = "hotelPickupArea") val hotelPickupArea: String = "Complimentary central Lisbon hotel & Airbnb pickup included."
)

@JsonClass(generateAdapter = true)
data class ApiHumanHandoffRequest(
    @Json(name = "customerName") val customerName: String? = null,
    @Json(name = "contactInfo") val contactInfo: String? = null,
    @Json(name = "reason") val reason: String,
    @Json(name = "conversationSummary") val conversationSummary: String? = null,
    @Json(name = "preferredLanguage") val preferredLanguage: String = "English"
)

@JsonClass(generateAdapter = true)
data class ApiHumanHandoffResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "ticketId") val ticketId: String? = null,
    @Json(name = "whatsappDirectUrl") val whatsappDirectUrl: String = "https://wa.me/351912345678",
    @Json(name = "message") val message: String = "A live TukTuk24 senior representative has been notified."
)
