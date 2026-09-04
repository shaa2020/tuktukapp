package com.example.data.repository

import com.example.data.model.*
import com.example.domain.ai.GeminiAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiAssistantRepository(
    private val tourRepository: TourRepository,
    private val geminiAiService: GeminiAiService = GeminiAiService(tourRepository.tuktuk24Repository)
) {

    suspend fun getAiRecommendation(
        userPrompt: String,
        history: List<AiMessage> = emptyList()
    ): AiMessage = withContext(Dispatchers.IO) {
        geminiAiService.generateChatResponse(history, userPrompt)
    }
}
