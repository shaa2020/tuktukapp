package com.example.data.remote.api

import retrofit2.Response
import retrofit2.http.*

interface TukTuk24ApiService {

    @GET("tours")
    suspend fun getTours(): Response<List<ApiTourDto>>

    @GET("tours/{id}")
    suspend fun getTourById(@Path("id") id: String): Response<ApiTourDto>

    @GET("tours/slug/{slug}")
    suspend fun getTourBySlug(@Path("slug") slug: String): Response<ApiTourDto>

    @GET("availability")
    suspend fun checkAvailability(
        @Query("tourId") tourId: String,
        @Query("date") date: String,
        @Query("time") time: String? = null,
        @Query("partySize") partySize: Int = 2
    ): Response<ApiAvailabilityResponse>

    @POST("leads")
    suspend fun createLead(
        @Body request: ApiLeadRequest
    ): Response<ApiLeadResponse>

    @POST("booking-requests")
    suspend fun createBookingRequest(
        @Body request: ApiBookingRequest
    ): Response<ApiBookingResponse>

    @GET("ai/tours")
    suspend fun getAiTours(): Response<List<ApiTourDto>>

    @GET("ai/business")
    suspend fun getBusinessInfo(): Response<ApiBusinessInfoDto>

    @POST("ai/human-handoff")
    suspend fun requestHumanHandoff(
        @Body request: ApiHumanHandoffRequest
    ): Response<ApiHumanHandoffResponse>
}
