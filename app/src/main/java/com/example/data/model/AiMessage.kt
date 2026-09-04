package com.example.data.model

enum class AiSender {
    USER,
    ASSISTANT
}

data class RecommendedTourRef(
    val tourId: String,
    val title: String,
    val destination: String,
    val priceEur: Double,
    val rating: Double,
    val imageUrl: String
)

data class AiMessage(
    val id: String,
    val sender: AiSender,
    val text: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val recommendedTours: List<RecommendedTourRef> = emptyList(),
    val isDisclaimer: Boolean = false
)
