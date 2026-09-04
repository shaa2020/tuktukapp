package com.example.data.model

enum class NotificationType {
    BOOKING_CONFIRMED,
    BOOKING_REMINDER,
    GUIDE_ASSIGNED,
    GUIDE_ARRIVING,
    TOUR_STARTING,
    TOUR_COMPLETED,
    REVIEW_REQUEST,
    PRICE_CHANGE,
    WISHLIST_AVAILABILITY,
    PROMOTION,
    NEW_EXPERIENCE
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedId: String? = null
)
