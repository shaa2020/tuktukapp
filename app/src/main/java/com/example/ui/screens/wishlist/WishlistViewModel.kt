package com.example.ui.screens.wishlist

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

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency

    private val _savedTours = MutableStateFlow<List<Tour>>(emptyList())
    val savedTours: StateFlow<List<Tour>> = _savedTours.asStateFlow()

    init {
        viewModelScope.launch {
            wishlistRepository.wishlistTourIds.collect { ids ->
                val allTours = tourRepository.getFeaturedTours() + tourRepository.getPopularTours() + tourRepository.getRecommendedTours()
                val uniqueTours = allTours.distinctBy { it.id }
                _savedTours.value = uniqueTours.filter { ids.contains(it.id) }
            }
        }
    }

    fun removeWishlist(tourId: String) {
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(tourId, false)
        }
    }
}
