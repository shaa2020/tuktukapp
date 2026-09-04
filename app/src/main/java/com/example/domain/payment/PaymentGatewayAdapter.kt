package com.example.domain.payment

import com.example.data.model.PaymentDetails
import com.example.data.model.PaymentProvider
import com.example.data.remote.PaymentResult

interface PaymentGatewayAdapter {
    val provider: PaymentProvider
    fun isConfigured(): Boolean
    fun getConfiguration(): GatewayConfiguration
    fun updateConfiguration(config: GatewayConfiguration)
    suspend fun executePayment(
        amountEur: Double,
        currencyCode: String,
        cardNumber: String? = null,
        cardHolder: String? = null,
        payPalEmail: String? = null
    ): PaymentResult
}
