package com.example

import com.example.data.model.TourCategory
import com.example.data.remote.api.ApiBookingRequest
import com.example.data.remote.api.ApiLeadRequest
import com.example.data.remote.api.TukTuk24ApiClient
import com.example.data.repository.TukTuk24Repository
import com.example.domain.ai.GeminiAiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TukTuk24ProductionApiTest {

    private lateinit var repository: TukTuk24Repository
    private lateinit var aiService: GeminiAiService

    @Before
    fun setup() {
        repository = TukTuk24Repository()
        aiService = GeminiAiService(repository)
    }

    @Test
    fun testApiClientConfiguration() {
        val baseUrl = TukTuk24ApiClient.getBaseUrl()
        assertTrue("Base URL should be valid", baseUrl.startsWith("http"))
        assertTrue("Base URL should end with slash", baseUrl.endsWith("/"))
    }

    @Test
    fun testToursAvailableInRepository() = runBlocking {
        val toursResult = repository.fetchLiveTours()
        assertTrue("Tours result should be successful", toursResult.isSuccess)
        val tours = toursResult.getOrThrow()
        assertTrue("Repository should provide tours", tours.isNotEmpty())

        val lisbonTour = repository.getTourById("tour_lisbon_7_hills")
        assertNotNull("Lisbon tour should exist", lisbonTour)
        assertEquals("Lisbon", lisbonTour?.destination)
        assertEquals(TourCategory.HISTORIC, lisbonTour?.category)
        assertTrue("Should have highlights", (lisbonTour?.highlights?.size ?: 0) > 0)
    }

    @Test
    fun testAvailabilityCheck() = runBlocking {
        val resp = repository.checkAvailability("tour_lisbon_7_hills", "2026-09-01", "10:00 AM", 2)
        assertTrue("Should return available response", resp.available)
        assertTrue("Available slots should be non-empty", resp.availableSlots.isNotEmpty())
    }

    @Test
    fun testLeadCreationPayload() = runBlocking {
        val leadReq = ApiLeadRequest(
            name = "Maria Gonzalez",
            email = "maria@example.com",
            phone = "+34612345678",
            language = "Spanish",
            partySize = 4,
            preferredDate = "2026-09-05",
            tourName = "Lisbon 7 Hills Private Experience",
            customerMessage = "Interested in afternoon slot with hotel pickup"
        )
        val resp = repository.createLead(leadReq)
        assertTrue("Lead creation should return success", resp.success)
        assertNotNull("Lead ID should be generated", resp.leadId)
    }

    @Test
    fun testBookingRequestPayload() = runBlocking {
        val bookingReq = ApiBookingRequest(
            name = "David Chen",
            email = "david@example.com",
            phone = "+14155551234",
            tourId = "tour_sintra_royal_palaces",
            date = "2026-09-10",
            time = "09:30 AM",
            partySize = 3,
            pickupLocation = "Four Seasons Hotel Ritz Lisbon",
            selectedLanguage = "English"
        )
        val resp = repository.createBookingRequest(bookingReq)
        assertTrue("Booking request should succeed", resp.success)
        assertNotNull("Confirmation code should be present", resp.confirmationCode)
        assertEquals("REQUESTED", resp.status)
    }

    @Test
    fun testBusinessInformationRetrieval() = runBlocking {
        val info = repository.getBusinessInfo()
        assertEquals("TukTuk24", info.brandName)
        assertTrue("Opening hours should be present", info.openingHours.isNotBlank())
        assertTrue("Timezone should specify Lisbon", info.timezone.contains("Lisbon"))
        assertTrue("Languages should include English and Portuguese", info.languagesSupported.contains("English") && info.languagesSupported.contains("Portuguese"))
    }

    @Test
    fun testMultilingualFallbackResponses() = runBlocking {
        // Portuguese test
        val ptResponse = aiService.generateChatResponse(emptyList(), "Olá, gostaria de saber o preço do passeio em Lisboa para 2 pessoas")
        assertTrue("Portuguese query should return Portuguese response", ptResponse.text.contains("Olá") || ptResponse.text.contains("TukTuk24"))
        assertTrue("Should include recommended tours", ptResponse.recommendedTours.isNotEmpty())

        // Spanish test
        val esResponse = aiService.generateChatResponse(emptyList(), "Hola, cuánto cuesta el tour a Sintra y Pena Palace?")
        assertTrue("Spanish query should return Spanish response", esResponse.text.contains("Hola") || esResponse.text.contains("TukTuk24"))

        // French test
        val frResponse = aiService.generateChatResponse(emptyList(), "Bonjour, quel est le meilleur tour au coucher du soleil?")
        assertTrue("French query should return French response", frResponse.text.contains("Bonjour") || frResponse.text.contains("TukTuk24"))
    }
}
