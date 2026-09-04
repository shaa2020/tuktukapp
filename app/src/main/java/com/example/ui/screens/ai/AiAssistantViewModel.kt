package com.example.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiMessage
import com.example.data.model.AiSender
import com.example.data.model.AppCurrency
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiAssistantViewModel(
    private val aiAssistantRepository: AiAssistantRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentCurrency: StateFlow<AppCurrency> = userRepository.currentCurrency

    private val _messages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                id = "welcome_msg",
                sender = AiSender.ASSISTANT,
                text = "Olá! I am your official TukTuk24 AI Tour Specialist for Portugal. How can I assist your journey today?\n\nAsk me anything in any language:\n• \"What are the best tours in Lisbon and Sintra?\"\n• \"Are there available sunset tours for 2 tomorrow?\"\n• \"Passeio de 3 horas em Lisboa com recolha no hotel\"\n• \"¿Cuál es la política de cancelación?\""
            )
        )
    )
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val userMsg = AiMessage(
            id = "user_" + System.currentTimeMillis(),
            sender = AiSender.USER,
            text = userText
        )

        val currentList = _messages.value + userMsg
        _messages.value = currentList
        _isLoading.value = true

        viewModelScope.launch {
            val responseMsg = aiAssistantRepository.getAiRecommendation(userText, currentList)
            _messages.value = _messages.value + responseMsg
            _isLoading.value = false
        }
    }

    fun requestHumanEscalation() {
        sendMessage("I would like to speak with a human TukTuk24 representative on WhatsApp or phone.")
    }
}
