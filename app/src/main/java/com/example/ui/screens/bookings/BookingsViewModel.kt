package com.example.ui.screens.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppCurrency
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.repository.BookingRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingsViewModel(
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _selectedBooking = MutableStateFlow<Booking?>(null)
    val selectedBooking: StateFlow<Booking?> = _selectedBooking.asStateFlow()

    private var currentBookingId: String? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed demo booking if database is empty
            bookingRepository.seedSampleBookingIfEmpty()
        }

        viewModelScope.launch {
            bookingRepository.allBookings.collect { list ->
                _bookings.value = list
                val targetId = currentBookingId ?: _selectedBooking.value?.bookingId
                if (targetId != null) {
                    val matched = list.find { it.bookingId == targetId }
                    if (matched != null) {
                        _selectedBooking.value = matched
                    }
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadBookingById(bookingId: String) {
        currentBookingId = bookingId
        val found = _bookings.value.find { it.bookingId == bookingId }
        if (found != null) {
            _selectedBooking.value = found
        }
        viewModelScope.launch {
            val fromDb = bookingRepository.getBookingById(bookingId)
            if (fromDb != null) {
                _selectedBooking.value = fromDb
            }
        }
    }

    fun rescheduleBooking(bookingId: String, newDate: String, newSlot: String) {
        viewModelScope.launch {
            bookingRepository.rescheduleBooking(bookingId, newDate, newSlot)
        }
    }

    fun updateBookingNotes(bookingId: String, notes: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingNotes(bookingId, notes)
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, BookingStatus.CANCELLED)
        }
    }

    fun setBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, status)
        }
    }

    fun advanceBookingStatus(bookingId: String) {
        val current = _selectedBooking.value ?: _bookings.value.find { it.bookingId == bookingId } ?: return
        val nextStatus = when (current.status) {
            BookingStatus.PENDING -> BookingStatus.CONFIRMED
            BookingStatus.CONFIRMED -> BookingStatus.GUIDE_ASSIGNED
            BookingStatus.GUIDE_ASSIGNED -> BookingStatus.GUIDE_ON_THE_WAY
            BookingStatus.GUIDE_ON_THE_WAY -> BookingStatus.STARTED
            BookingStatus.STARTED -> BookingStatus.COMPLETED
            BookingStatus.COMPLETED -> BookingStatus.CONFIRMED // Loop back for demo
            BookingStatus.CANCELLED -> BookingStatus.CONFIRMED
            BookingStatus.REFUNDED -> BookingStatus.CONFIRMED
        }
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, nextStatus)
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(bookingId)
            if (_selectedBooking.value?.bookingId == bookingId) {
                _selectedBooking.value = null
            }
        }
    }
}
