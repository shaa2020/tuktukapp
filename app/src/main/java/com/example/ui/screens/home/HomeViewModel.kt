package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppCurrency
import com.example.data.model.AppLanguage
import com.example.data.model.Tour
import com.example.data.repository.FilterParams
import com.example.data.repository.TourRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tourRepository: TourRepository,
    private val wishlistRepository: WishlistRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency
    val currentLanguage: StateFlow<AppLanguage> = userRepository.currentLanguage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedQuickSearch = MutableStateFlow<String?>(null)
    val selectedQuickSearch: StateFlow<String?> = _selectedQuickSearch.asStateFlow()

    private val _wishlistedIds = MutableStateFlow<Set<String>>(emptySet())
    val wishlistedIds: StateFlow<Set<String>> = _wishlistedIds.asStateFlow()

    init {
        viewModelScope.launch {
            wishlistRepository.wishlistTourIds.collect { ids ->
                _wishlistedIds.value = ids.toSet()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onQuickSearchClick(dest: String) {
        if (_selectedQuickSearch.value == dest) {
            _selectedQuickSearch.value = null
        } else {
            _selectedQuickSearch.value = dest
        }
    }

    fun toggleWishlist(tourId: String) {
        viewModelScope.launch {
            val isCurrentlySaved = _wishlistedIds.value.contains(tourId)
            wishlistRepository.toggleWishlist(tourId, !isCurrentlySaved)
        }
    }

    fun updateCurrency(currency: AppCurrency) {
        userRepository.updateCurrency(currency)
    }

    fun updateLanguage(language: AppLanguage) {
        userRepository.updateLanguage(language)
    }

    fun getPopularTours(): List<Tour> = tourRepository.getPopularTours()
    fun getRecommendedTours(): List<Tour> = tourRepository.getRecommendedTours()
    fun getFeaturedTours(): List<Tour> = tourRepository.getFeaturedTours()
    fun getLastMinuteTours(): List<Tour> = tourRepository.getLastMinuteTours()
}
