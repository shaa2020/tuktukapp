package com.example.data.repository

import com.example.data.local.WishlistDao
import com.example.data.local.WishlistEntity
import kotlinx.coroutines.flow.Flow

class WishlistRepository(private val wishlistDao: WishlistDao) {

    val wishlistTourIds: Flow<List<String>> = wishlistDao.getAllWishlistTourIds()

    fun isWishlisted(tourId: String): Flow<Boolean> = wishlistDao.isWishlisted(tourId)

    suspend fun toggleWishlist(tourId: String, isSaved: Boolean) {
        if (isSaved) {
            wishlistDao.insertWishlistItem(WishlistEntity(tourId = tourId))
        } else {
            wishlistDao.removeWishlistItem(tourId)
        }
    }
}
