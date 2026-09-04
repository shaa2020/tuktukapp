package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.model.*
import com.example.data.remote.PaymentResult
import com.example.data.remote.PaymentService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class BookingRepository(
    private val bookingDao: BookingDao,
    private val paymentService: PaymentService
) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val bookingAdapter = moshi.adapter(Booking::class.java)

    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings().map { entities ->
        entities.mapNotNull { entity ->
            try {
                val parsed = bookingAdapter.fromJson(entity.rawJsonData)
                val resolvedStatus = try {
                    BookingStatus.valueOf(entity.statusName)
                } catch (e: Exception) {
                    parsed?.status ?: BookingStatus.CONFIRMED
                }
                parsed?.copy(
                    bookingDate = entity.bookingDate,
                    timeSlot = entity.timeSlot,
                    status = resolvedStatus
                )
            } catch (e: Exception) {
                // Fallback manual mapping
                Booking(
                    bookingId = entity.bookingId,
                    tourId = entity.tourId,
                    tourTitle = entity.tourTitle,
                    tourImageUrl = entity.tourImageUrl,
                    destination = entity.destination,
                    bookingDate = entity.bookingDate,
                    timeSlot = entity.timeSlot,
                    guestCount = entity.guestCount,
                    selectedLanguage = entity.selectedLanguage,
                    pickupLocationName = entity.pickupLocationName,
                    selectedExtras = emptyList(),
                    customerName = entity.customerName,
                    customerEmail = entity.customerEmail,
                    customerPhone = entity.customerPhone,
                    priceBreakdown = PriceBreakdown.calculate(entity.basePriceEur, 15.0, entity.guestCount, 0.0, 0.0),
                    status = try { BookingStatus.valueOf(entity.statusName) } catch (e: Exception) { BookingStatus.CONFIRMED },
                    qrCodePayload = entity.qrCodePayload
                )
            }
        }
    }

    suspend fun getBookingById(bookingId: String): Booking? {
        val entity = bookingDao.getBookingByIdOnce(bookingId) ?: return null
        return try {
            val parsed = bookingAdapter.fromJson(entity.rawJsonData)
            val resolvedStatus = try {
                BookingStatus.valueOf(entity.statusName)
            } catch (e: Exception) {
                parsed?.status ?: BookingStatus.CONFIRMED
            }
            parsed?.copy(
                bookingDate = entity.bookingDate,
                timeSlot = entity.timeSlot,
                status = resolvedStatus
            )
        } catch (e: Exception) {
            Booking(
                bookingId = entity.bookingId,
                tourId = entity.tourId,
                tourTitle = entity.tourTitle,
                tourImageUrl = entity.tourImageUrl,
                destination = entity.destination,
                bookingDate = entity.bookingDate,
                timeSlot = entity.timeSlot,
                guestCount = entity.guestCount,
                selectedLanguage = entity.selectedLanguage,
                pickupLocationName = entity.pickupLocationName,
                selectedExtras = emptyList(),
                customerName = entity.customerName,
                customerEmail = entity.customerEmail,
                customerPhone = entity.customerPhone,
                priceBreakdown = PriceBreakdown.calculate(entity.basePriceEur, 15.0, entity.guestCount, 0.0, 0.0),
                status = try { BookingStatus.valueOf(entity.statusName) } catch (e: Exception) { BookingStatus.CONFIRMED },
                qrCodePayload = entity.qrCodePayload
            )
        }
    }

    suspend fun createAndProcessBooking(
        tour: Tour,
        bookingDate: String,
        timeSlot: String,
        guestCount: Int,
        selectedLanguage: String,
        pickupLocation: PickupPoint,
        selectedExtras: List<TourExtra>,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        paymentProvider: PaymentProvider,
        promoCode: String? = null,
        notes: String? = null
    ): Result<Booking> {
        val extrasTotal = selectedExtras.sumOf { it.priceEur }
        val pickupFee = pickupLocation.extraFeeEur
        val discount = if (promoCode?.trim()?.uppercase() == "TUK2026") 15.0 else 0.0

        val priceBreakdown = PriceBreakdown.calculate(
            basePriceEur = tour.basePriceEur,
            perGuestPriceEur = tour.perGuestPriceEur,
            guestCount = guestCount,
            selectedExtrasTotal = extrasTotal,
            pickupFeeEur = pickupFee,
            promoDiscountEur = discount
        )

        val paymentResult = paymentService.processPayment(
            provider = paymentProvider,
            amountEur = priceBreakdown.finalTotalEur,
            currencyCode = "EUR"
        )

        return when (paymentResult) {
            is PaymentResult.Success -> {
                val bookingId = "TT24-" + (10000..99999).random()
                val qrPayload = "TUKTUK24|TICKET|$bookingId|$customerName|${tour.id}|$bookingDate"

                val newBooking = Booking(
                    bookingId = bookingId,
                    tourId = tour.id,
                    tourTitle = tour.title,
                    tourImageUrl = tour.mainImageUrl,
                    destination = tour.destination,
                    bookingDate = bookingDate,
                    timeSlot = timeSlot,
                    guestCount = guestCount,
                    selectedLanguage = selectedLanguage,
                    pickupLocationName = pickupLocation.name,
                    selectedExtras = selectedExtras,
                    customerName = customerName,
                    customerEmail = customerEmail,
                    customerPhone = customerPhone,
                    priceBreakdown = priceBreakdown,
                    status = BookingStatus.CONFIRMED,
                    qrCodePayload = qrPayload,
                    guideInfo = tour.assignedGuide,
                    paymentDetails = paymentResult.details,
                    notes = notes
                )

                saveBookingToLocalDb(newBooking)
                Result.success(newBooking)
            }
            is PaymentResult.Failure -> {
                Result.failure(Exception(paymentResult.errorMessage))
            }
        }
    }

    suspend fun saveBookingToLocalDb(booking: Booking) {
        val json = bookingAdapter.toJson(booking)
        val entity = BookingEntity(
            bookingId = booking.bookingId,
            tourId = booking.tourId,
            tourTitle = booking.tourTitle,
            tourImageUrl = booking.tourImageUrl,
            destination = booking.destination,
            bookingDate = booking.bookingDate,
            timeSlot = booking.timeSlot,
            guestCount = booking.guestCount,
            selectedLanguage = booking.selectedLanguage,
            pickupLocationName = booking.pickupLocationName,
            customerName = booking.customerName,
            customerEmail = booking.customerEmail,
            customerPhone = booking.customerPhone,
            basePriceEur = booking.priceBreakdown.basePriceEur,
            finalTotalEur = booking.priceBreakdown.finalTotalEur,
            statusName = booking.status.name,
            qrCodePayload = booking.qrCodePayload,
            createdAtMs = booking.createdAtMs,
            rawJsonData = json
        )
        bookingDao.insertOrUpdateBooking(entity)
    }

    suspend fun updateBookingStatus(bookingId: String, newStatus: BookingStatus) {
        val entity = bookingDao.getBookingByIdOnce(bookingId)
        if (entity != null) {
            val updatedJson = try {
                val parsed = bookingAdapter.fromJson(entity.rawJsonData)
                if (parsed != null) {
                    bookingAdapter.toJson(parsed.copy(status = newStatus))
                } else entity.rawJsonData
            } catch (e: Exception) {
                entity.rawJsonData
            }
            bookingDao.updateBookingStatusAndJson(bookingId, newStatus.name, updatedJson)
        } else {
            bookingDao.updateBookingStatus(bookingId, newStatus.name)
        }
    }

    suspend fun rescheduleBooking(bookingId: String, newDate: String, newSlot: String) {
        val entity = bookingDao.getBookingByIdOnce(bookingId)
        if (entity != null) {
            val updatedJson = try {
                val parsed = bookingAdapter.fromJson(entity.rawJsonData)
                if (parsed != null) {
                    bookingAdapter.toJson(parsed.copy(bookingDate = newDate, timeSlot = newSlot))
                } else entity.rawJsonData
            } catch (e: Exception) {
                entity.rawJsonData
            }
            bookingDao.rescheduleBooking(bookingId, newDate, newSlot, updatedJson)
        }
    }

    suspend fun updateBookingNotes(bookingId: String, notes: String) {
        val entity = bookingDao.getBookingByIdOnce(bookingId)
        if (entity != null) {
            try {
                val parsed = bookingAdapter.fromJson(entity.rawJsonData)
                if (parsed != null) {
                    val updatedBooking = parsed.copy(notes = notes)
                    saveBookingToLocalDb(updatedBooking)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    suspend fun assignGuideToBooking(bookingId: String, guide: GuideInfo) {
        val entity = bookingDao.getBookingByIdOnce(bookingId)
        if (entity != null) {
            try {
                val parsed = bookingAdapter.fromJson(entity.rawJsonData)
                if (parsed != null) {
                    val updatedBooking = parsed.copy(
                        guideInfo = guide,
                        status = if (parsed.status == BookingStatus.CONFIRMED || parsed.status == BookingStatus.PENDING) {
                            BookingStatus.GUIDE_ASSIGNED
                        } else {
                            parsed.status
                        }
                    )
                    saveBookingToLocalDb(updatedBooking)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    suspend fun checkInTicket(ticketPayloadOrId: String): Result<Booking> {
        val cleanInput = ticketPayloadOrId.trim()
        // Extract bookingId if in TUKTUK24|TICKET|TT24-XXXX format
        val extractedId = if (cleanInput.contains("|")) {
            cleanInput.split("|").find { it.startsWith("TT24-") } ?: cleanInput
        } else {
            cleanInput
        }

        val entity = bookingDao.getBookingByIdOnce(extractedId)
            ?: return Result.failure(Exception("Ticket $extractedId not found in Lisbon database."))

        return try {
            val parsed = bookingAdapter.fromJson(entity.rawJsonData)
                ?: return Result.failure(Exception("Failed to decode ticket data."))

            val updatedBooking = parsed.copy(status = BookingStatus.STARTED)
            saveBookingToLocalDb(updatedBooking)
            Result.success(updatedBooking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBooking(bookingId: String) {
        bookingDao.deleteBooking(bookingId)
    }

    suspend fun seedSampleBookingIfEmpty() {
        if (bookingDao.getCount() == 0) {
            val sampleBooking = Booking(
                bookingId = "TT24-88412",
                tourId = "tour_1",
                tourTitle = "Historic Alfama & Castelo Hills Electric Tour",
                tourImageUrl = "https://images.unsplash.com/photo-1585208798174-6cedd86e019a?auto=format&fit=crop&w=800&q=80",
                destination = "Lisbon, Portugal",
                bookingDate = "2026-08-16",
                timeSlot = "10:30 AM",
                guestCount = 2,
                selectedLanguage = "English",
                pickupLocationName = "Terreiro do Paço Central Gate (Commerce Square)",
                selectedExtras = listOf(
                    TourExtra(
                        id = "extra_pasteis",
                        name = "Pastéis de Belém Tasting Pack",
                        description = "Box of 4 fresh warm custard tarts + cinnamon shaker",
                        priceEur = 8.0,
                        iconName = "Restaurant"
                    )
                ),
                customerName = "Alex Rivera",
                customerEmail = "alex.rivera@example.com",
                customerPhone = "+351 910 000 000",
                priceBreakdown = PriceBreakdown(
                    basePriceEur = 65.0,
                    guestCount = 2,
                    guestPriceEur = 15.0,
                    extrasPriceEur = 8.0,
                    pickupFeeEur = 0.0,
                    taxesAndFeesEur = 5.28,
                    discountAmountEur = 0.0,
                    finalTotalEur = 93.28
                ),
                status = BookingStatus.CONFIRMED,
                qrCodePayload = "TUKTUK24|TICKET|TT24-88412|Alex Rivera|tour_1|2026-08-16",
                guideInfo = GuideInfo(
                    id = "guide_diogo",
                    name = "Diogo Silva",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                    rating = 4.9,
                    reviewCount = 320,
                    languages = listOf("Portuguese", "English", "Spanish"),
                    bio = "Lisbon native & licensed eco-tour chauffeur. Passionate about 12th-century Alfama history, Fado lore, and secret miradouros.",
                    phone = "+351 912 345 678",
                    whatsapp = "+351912345678"
                ),
                paymentDetails = PaymentDetails(
                    paymentId = "pay_live_8829",
                    provider = PaymentProvider.CARD,
                    transactionRef = "TX-8829-LIS",
                    cardLast4 = "4242",
                    isVerifiedServerSide = true
                ),
                notes = "Celebrating anniversary. Would love a scenic photo stop at Miradouro de Santa Luzia."
            )
            saveBookingToLocalDb(sampleBooking)
        }
    }
}
