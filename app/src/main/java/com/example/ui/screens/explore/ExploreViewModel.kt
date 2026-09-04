package com.example.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppCurrency
import com.example.data.model.Tour
import com.example.data.repository.FilterParams
import com.example.data.repository.TourRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val tourRepository: TourRepository,
    private val wishlistRepository: WishlistRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency

    private val _filterParams = MutableStateFlow(FilterParams())
    val filterParams: StateFlow<FilterParams> = _filterParams.asStateFlow()

    private val _filteredTours = MutableStateFlow<List<Tour>>(emptyList())
    val filteredTours: StateFlow<List<Tour>> = _filteredTours.asStateFlow()

    private val _wishlistedIds = MutableStateFlow<Set<String>>(emptySet())
    val wishlistedIds: StateFlow<Set<String>> = _wishlistedIds.asStateFlow()

    init {
        viewModelScope.launch {
            wishlistRepository.wishlistTourIds.collect { ids ->
                _wishlistedIds.value = ids.toSet()
            }
        }
        updateFilteredList()
    }

    fun setInitialDestination(dest: String?) {
        if (!dest.isNull_or_blank()) {
            _filterParams.value = _filterParams.value.copy(destination = dest)
            updateFilteredList()
        }
    }

    fun updateQuery(query: String) {
        _filterParams.value = _filterParams.value.copy(query = query)
        updateFilteredList()
    }

    fun applyFilters(newParams: FilterParams) {
        _filterParams.value = newParams
        updateFilteredList()
    }

    fun toggleWishlist(tourId: String) {
        viewModelScope.launch {
            val isCurrentlySaved = _wishlistedIds.value.contains(tourId)
            wishlistRepository.toggleWishlist(tourId, !isCurrentlySaved)
        }
    }

    private fun updateFilteredList() {
        _filteredTours.value = tourRepository.getFilteredTours(_filterParams.value)
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
