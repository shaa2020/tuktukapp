package com.example.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.BookingRepository
import com.example.data.repository.TourRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Processing : BookingUiState()
    data class Success(val booking: Booking) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

class BookingViewModel(
    private val tourRepository: TourRepository,
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency
    val userProfile: StateFlow<UserProfile> = userRepository.userProfile

    private val _tour = MutableStateFlow<Tour?>(null)
    val tour: StateFlow<Tour?> = _tour.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    // Form states
    private val _selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow("10:30 AM")
    val selectedTimeSlot: StateFlow<String> = _selectedTimeSlot.asStateFlow()

    private val _guestCount = MutableStateFlow(2)
    val guestCount: StateFlow<Int> = _guestCount.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedPickup = MutableStateFlow<PickupPoint?>(null)
    val selectedPickup: StateFlow<PickupPoint?> = _selectedPickup.asStateFlow()

    private val _selectedExtras = MutableStateFlow<Set<TourExtra>>(emptySet())
    val selectedExtras: StateFlow<Set<TourExtra>> = _selectedExtras.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerEmail = MutableStateFlow("")
    val customerEmail: StateFlow<String> = _customerEmail.asStateFlow()

    private val _customerPhone = MutableStateFlow("")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _selectedPaymentProvider = MutableStateFlow(PaymentProvider.CARD)
    val selectedPaymentProvider: StateFlow<PaymentProvider> = _selectedPaymentProvider.asStateFlow()

    private val _promoCode = MutableStateFlow("")
    val promoCode: StateFlow<String> = _promoCode.asStateFlow()

    fun resetBookingState() {
        _bookingState.value = BookingUiState.Idle
    }

    fun selectSavedTraveler(traveler: SavedTraveler) {
        _customerName.value = traveler.fullName
        _customerEmail.value = traveler.email
        _customerPhone.value = traveler.phone
    }

    fun setNotes(notes: String) {
        _notes.value = notes
    }

    fun loginWithGoogle() {
        userRepository.loginWithGoogle()
        val profile = userRepository.userProfile.value
        _customerName.value = profile.fullName
        _customerEmail.value = profile.email
        _customerPhone.value = profile.phone
    }

    fun loginWithApple() {
        userRepository.loginWithApple()
        val profile = userRepository.userProfile.value
        _customerName.value = profile.fullName
        _customerEmail.value = profile.email
        _customerPhone.value = profile.phone
    }

    fun loadTour(tourId: String) {
        val found = tourRepository.getTourById(tourId)
        _tour.value = found
        if (found != null && found.pickupPoints.isNotEmpty()) {
            _selectedPickup.value = found.pickupPoints.first()
        }

        // Auto-fill from user profile
        val profile = userRepository.userProfile.value
        if (_customerName.value.isBlank()) _customerName.value = profile.fullName
        if (_customerEmail.value.isBlank()) _customerEmail.value = profile.email
        if (_customerPhone.value.isBlank()) _customerPhone.value = profile.phone
    }

    fun setDate(date: String) { _selectedDate.value = date }
    fun setTimeSlot(slot: String) { _selectedTimeSlot.value = slot }
    fun setGuestCount(count: Int) { _guestCount.value = count.coerceAtLeast(1) }
    fun setLanguage(lang: String) { _selectedLanguage.value = lang }
    fun setPickup(pickup: PickupPoint) { _selectedPickup.value = pickup }
    
    fun toggleExtra(extra: TourExtra) {
        val current = _selectedExtras.value.toMutableSet()
        if (current.contains(extra)) current.remove(extra) else current.add(extra)
        _selectedExtras.value = current
    }

    fun setCustomerInfo(name: String, email: String, phone: String) {
        _customerName.value = name
        _customerEmail.value = email
        _customerPhone.value = phone
    }

    fun setPaymentProvider(provider: PaymentProvider) {
        _selectedPaymentProvider.value = provider
    }

    fun setPromoCode(code: String) {
        _promoCode.value = code
    }

    fun calculatePriceBreakdown(): PriceBreakdown? {
        val t = _tour.value ?: return null
        val extrasTotal = _selectedExtras.value.sumOf { it.priceEur }
        val pickupFee = _selectedPickup.value?.extraFeeEur ?: 0.0
        val discount = if (_promoCode.value.trim().uppercase() == "TUK2026") 15.0 else 0.0

        return PriceBreakdown.calculate(
            basePriceEur = t.basePriceEur,
            perGuestPriceEur = t.perGuestPriceEur,
            guestCount = _guestCount.value,
            selectedExtrasTotal = extrasTotal,
            pickupFeeEur = pickupFee,
            promoDiscountEur = discount
        )
    }

    fun submitBooking() {
        val t = _tour.value ?: return
        val pickup = _selectedPickup.value ?: return
        if (_customerName.value.isBlank() || _customerEmail.value.isBlank()) {
            _bookingState.value = BookingUiState.Error("Please provide your name and contact email.")
            return
        }

        _bookingState.value = BookingUiState.Processing

        viewModelScope.launch {
            val result = bookingRepository.createAndProcessBooking(
                tour = t,
                bookingDate = _selectedDate.value,
                timeSlot = _selectedTimeSlot.value,
                guestCount = _guestCount.value,
                selectedLanguage = _selectedLanguage.value,
                pickupLocation = pickup,
                selectedExtras = _selectedExtras.value.toList(),
                customerName = _customerName.value,
                customerEmail = _customerEmail.value,
                customerPhone = _customerPhone.value,
                paymentProvider = _selectedPaymentProvider.value,
                promoCode = _promoCode.value,
                notes = _notes.value.takeIf { it.isNotBlank() }
            )

            result.fold(
                onSuccess = { booking ->
                    _bookingState.value = BookingUiState.Success(booking)
                },
                onFailure = { err ->
                    _bookingState.value = BookingUiState.Error(err.message ?: "Booking payment failed.")
                }
            )
        }
    }
}
