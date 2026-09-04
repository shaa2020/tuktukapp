package com.example.data.remote

import com.example.data.model.PaymentDetails
import com.example.data.model.PaymentProvider
import kotlinx.coroutines.delay
import java.util.UUID

sealed class PaymentResult {
    data class Success(val details: PaymentDetails) : PaymentResult()
    data class Failure(val errorMessage: String) : PaymentResult()
}

data class PayPalCredentials(
    val clientId: String = "sb-client-id-tuk24-lisbon-live-sandbox",
    val secretKey: String = "sb-secret-key-tuk24-express-pass",
    val merchantEmail: String = "paypal-checkout@tuktuk24.pt",
    val isLiveMode: Boolean = false
)

interface PaymentService {
    fun getPayPalCredentials(): PayPalCredentials
    fun updatePayPalCredentials(clientId: String, secretKey: String, isLiveMode: Boolean)
    suspend fun processPayment(
        provider: PaymentProvider,
        amountEur: Double,
        currencyCode: String,
        cardNumber: String? = null,
        cardHolder: String? = null,
        payPalEmail: String? = null
    ): PaymentResult
}

class SimulatedPaymentService : PaymentService {

    private var paypalCreds = PayPalCredentials()

    override fun getPayPalCredentials(): PayPalCredentials {
        return paypalCreds
    }

    override fun updatePayPalCredentials(clientId: String, secretKey: String, isLiveMode: Boolean) {
        paypalCreds = paypalCreds.copy(
            clientId = clientId.ifBlank { paypalCreds.clientId },
            secretKey = secretKey.ifBlank { paypalCreds.secretKey },
            isLiveMode = isLiveMode
        )
    }

    override suspend fun processPayment(
        provider: PaymentProvider,
        amountEur: Double,
        currencyCode: String,
        cardNumber: String?,
        cardHolder: String?,
        payPalEmail: String?
    ): PaymentResult {
        // Simulate network API request and server-side payment verification
        delay(1200)

        // Basic verification checks
        if (amountEur <= 0.0) {
            return PaymentResult.Failure("Invalid payment amount.")
        }

        val last4 = if (!cardNumber.isNullOrBlank() && cardNumber.length >= 4) {
            cardNumber.takeLast(4)
        } else if (provider == PaymentProvider.PAYPAL) {
            "PAYPAL"
        } else {
            "8821"
        }

        val transactionId = when (provider) {
            PaymentProvider.MB_WAY -> "MBWAY-" + UUID.randomUUID().toString().take(8).uppercase()
            PaymentProvider.PAYPAL -> "PP-EXPRESS-" + UUID.randomUUID().toString().take(10).uppercase()
            PaymentProvider.APPLE_PAY -> "APL-PAY-" + UUID.randomUUID().toString().take(8).uppercase()
            PaymentProvider.GOOGLE_PAY -> "GGL-PAY-" + UUID.randomUUID().toString().take(8).uppercase()
            else -> "TXN-" + UUID.randomUUID().toString().take(8).uppercase()
        }

        val details = PaymentDetails(
            paymentId = "PAY-" + UUID.randomUUID().toString().take(8).uppercase(),
            provider = provider,
            transactionRef = transactionId,
            cardLast4 = last4,
            isVerifiedServerSide = true
        )

        return PaymentResult.Success(details)
    }
}

