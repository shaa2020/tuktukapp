package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val tourId: String,
    val savedAtMs: Long = System.currentTimeMillis()
)

@Dao
interface WishlistDao {
    @Query("SELECT tourId FROM wishlist_items ORDER BY savedAtMs DESC")
    fun getAllWishlistTourIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistEntity)

    @Query("DELETE FROM wishlist_items WHERE tourId = :tourId")
    suspend fun removeWishlistItem(tourId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE tourId = :tourId)")
    fun isWishlisted(tourId: String): Flow<Boolean>
}
