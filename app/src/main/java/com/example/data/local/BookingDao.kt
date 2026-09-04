package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "local_bookings")
data class BookingEntity(
    @PrimaryKey val bookingId: String,
    val tourId: String,
    val tourTitle: String,
    val tourImageUrl: String,
    val destination: String,
    val bookingDate: String,
    val timeSlot: String,
    val guestCount: Int,
    val selectedLanguage: String,
    val pickupLocationName: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val basePriceEur: Double,
    val finalTotalEur: Double,
    val statusName: String,
    val qrCodePayload: String,
    val createdAtMs: Long = System.currentTimeMillis(),
    val rawJsonData: String
)

@Dao
interface BookingDao {
    @Query("SELECT * FROM local_bookings ORDER BY createdAtMs DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM local_bookings WHERE bookingId = :bookingId")
    fun getBookingById(bookingId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM local_bookings WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getBookingByIdOnce(bookingId: String): BookingEntity?

    @Query("SELECT COUNT(*) FROM local_bookings")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBooking(booking: BookingEntity)

    @Query("UPDATE local_bookings SET statusName = :statusName, rawJsonData = :rawJson WHERE bookingId = :bookingId")
    suspend fun updateBookingStatusAndJson(bookingId: String, statusName: String, rawJson: String)

    @Query("UPDATE local_bookings SET statusName = :statusName WHERE bookingId = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, statusName: String)

    @Query("UPDATE local_bookings SET bookingDate = :date, timeSlot = :slot, rawJsonData = :rawJson WHERE bookingId = :bookingId")
    suspend fun rescheduleBooking(bookingId: String, date: String, slot: String, rawJson: String)

    @Query("DELETE FROM local_bookings WHERE bookingId = :bookingId")
    suspend fun deleteBooking(bookingId: String)
}
