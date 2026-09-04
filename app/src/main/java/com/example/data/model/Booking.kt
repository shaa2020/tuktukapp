package com.example.data.model

enum class BookingStatus(val displayName: String, val badgeColorHex: Long) {
    PENDING("Booking Received", 0xFFEAB308),
    CONFIRMED("Confirmed", 0xFF3B82F6),
    GUIDE_ASSIGNED("Guide Assigned", 0xFF8B5CF6),
    GUIDE_ON_THE_WAY("Guide On The Way", 0xFF06B6D4),
    STARTED("Tour Started", 0xFF10B981),
    COMPLETED("Completed", 0xFF059669),
    CANCELLED("Cancelled", 0xFFEF4444),
    REFUNDED("Refunded", 0xFF6B7280)
}

enum class PaymentProvider(val displayName: String) {
    CARD("Credit/Debit Card"),
    MB_WAY("MB WAY (Portugal)"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay"),
    PAYPAL("PayPal"),
    SUMUP("SumUp Terminal"),
    CASH_ON_ARRIVAL("Cash on Arrival")
}

data class PaymentDetails(
    val paymentId: String,
    val provider: PaymentProvider,
    val transactionRef: String,
    val cardLast4: String? = null,
    val isVerifiedServerSide: Boolean = true,
    val timestampMs: Long = System.currentTimeMillis()
)

data class PriceBreakdown(
    val basePriceEur: Double,
    val guestCount: Int,
    val guestPriceEur: Double,
    val extrasPriceEur: Double,
    val pickupFeeEur: Double,
    val taxesAndFeesEur: Double,
    val discountAmountEur: Double,
    val finalTotalEur: Double
) {
    companion object {
        fun calculate(
            basePriceEur: Double,
            perGuestPriceEur: Double,
            guestCount: Int,
            selectedExtrasTotal: Double,
            pickupFeeEur: Double,
            promoDiscountEur: Double = 0.0
        ): PriceBreakdown {
            val guestPriceEur = (guestCount - 1).coerceAtLeast(0) * perGuestPriceEur
            val subtotal = basePriceEur + guestPriceEur + selectedExtrasTotal + pickupFeeEur
            val tax = subtotal * 0.06 // 6% Portuguese VAT for tourism
            val finalTotal = (subtotal + tax - promoDiscountEur).coerceAtLeast(0.0)
            return PriceBreakdown(
                basePriceEur = basePriceEur,
                guestCount = guestCount,
                guestPriceEur = guestPriceEur,
                extrasPriceEur = selectedExtrasTotal,
                pickupFeeEur = pickupFeeEur,
                taxesAndFeesEur = tax,
                discountAmountEur = promoDiscountEur,
                finalTotalEur = finalTotal
            )
        }
    }
}

data class Booking(
    val bookingId: String, // Unique immutable ID e.g. "TT24-98421"
    val tourId: String,
    val tourTitle: String,
    val tourImageUrl: String,
    val destination: String,
    val bookingDate: String, // YYYY-MM-DD
    val timeSlot: String,   // e.g. "10:00 AM"
    val guestCount: Int,
    val selectedLanguage: String,
    val pickupLocationName: String,
    val selectedExtras: List<TourExtra>,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val priceBreakdown: PriceBreakdown,
    val status: BookingStatus,
    val qrCodePayload: String,
    val guideInfo: GuideInfo? = null,
    val paymentDetails: PaymentDetails? = null,
    val createdAtMs: Long = System.currentTimeMillis(),
    val notes: String? = null
)
