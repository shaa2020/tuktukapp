package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Explore : Screen("explore", "Explore")
    object Wishlist : Screen("wishlist", "Wishlist")
    object Bookings : Screen("bookings", "Bookings")
    object Profile : Screen("profile", "Profile")
    object Auth : Screen("auth", "Sign In")
    object AiAssistant : Screen("ai_assistant", "AI Assistant")

    object TourDetail : Screen("tour_detail/{tourId}", "Tour Details") {
        fun createRoute(tourId: String) = "tour_detail/$tourId"
    }

    object BookingEngine : Screen("booking_engine/{tourId}", "Book Tour") {
        fun createRoute(tourId: String) = "booking_engine/$tourId"
    }

    object BookingDetail : Screen("booking_detail/{bookingId}", "Booking Details") {
        fun createRoute(bookingId: String) = "booking_detail/$bookingId"
    }
}
