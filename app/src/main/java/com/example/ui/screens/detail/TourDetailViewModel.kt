package com.example.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppCurrency
import com.example.data.model.Tour
import com.example.data.repository.TourRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TourDetailViewModel(
    private val tourRepository: TourRepository,
    private val wishlistRepository: WishlistRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency

    private val _tour = MutableStateFlow<Tour?>(null)
    val tour: StateFlow<Tour?> = _tour.asStateFlow()

    private val _isWishlisted = MutableStateFlow(false)
    val isWishlisted: StateFlow<Boolean> = _isWishlisted.asStateFlow()

    fun loadTour(tourId: String) {
        val foundTour = tourRepository.getTourById(tourId)
        _tour.value = foundTour

        viewModelScope.launch {
            wishlistRepository.wishlistTourIds.collect { ids ->
                _isWishlisted.value = ids.contains(tourId)
            }
        }
    }

    fun toggleWishlist() {
        val currentTour = _tour.value ?: return
        viewModelScope.launch {
            val nextState = !_isWishlisted.value
            wishlistRepository.toggleWishlist(currentTour.id, nextState)
        }
    }
}
