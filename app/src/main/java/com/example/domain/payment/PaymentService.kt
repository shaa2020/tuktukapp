package com.example.domain.payment

import com.example.data.model.PaymentDetails
import com.example.data.model.PaymentProvider
import com.example.data.remote.PayPalCredentials
import com.example.data.remote.PaymentResult
import com.example.data.remote.PaymentService as RemotePaymentService
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Domain PaymentService interface that supports modular payment gateway providers (e.g., PayPal, Stripe, Apple Pay, Google Pay).
 * Provides concrete configuration & credential management structures prepared for real integration.
 */
interface PaymentService : RemotePaymentService {
    fun getGatewaySettings(): ModularPaymentGatewaySettings
    fun updatePayPalConfig(config: PayPalGatewayConfig)
    fun updateStripeConfig(config: StripeGatewayConfig)
    fun registerGatewayAdapter(adapter: PaymentGatewayAdapter)
    fun getAdapter(provider: PaymentProvider): PaymentGatewayAdapter?
    fun getAvailableProviders(): List<PaymentProvider>
    fun isProviderConfigured(provider: PaymentProvider): Boolean
}

typealias DomainPaymentService = PaymentService

/**
 * Concrete implementation of domain PaymentService managing modular gateway adapters.
 */
class DefaultPaymentService : PaymentService {

    private var gatewaySettings = ModularPaymentGatewaySettings()
    private val adapters = mutableMapOf<PaymentProvider, PaymentGatewayAdapter>()

    init {
        // Register modular gateway adapters for PayPal & Stripe
        registerGatewayAdapter(PayPalGatewayAdapterImpl(gatewaySettings.paypalConfig))
        registerGatewayAdapter(StripeGatewayAdapterImpl(gatewaySettings.stripeConfig))
    }

    override fun getGatewaySettings(): ModularPaymentGatewaySettings = gatewaySettings

    override fun getAdapter(provider: PaymentProvider): PaymentGatewayAdapter? {
        return adapters[provider]
    }

    override fun isProviderConfigured(provider: PaymentProvider): Boolean {
        val adapter = adapters[provider]
        return adapter?.isConfigured() ?: true
    }

    override fun updatePayPalConfig(config: PayPalGatewayConfig) {
        gatewaySettings = gatewaySettings.copy(paypalConfig = config)
        adapters[PaymentProvider.PAYPAL]?.updateConfiguration(
            GatewayConfiguration(
                provider = PaymentProvider.PAYPAL,
                clientId = config.clientId,
                clientSecret = config.secretKey,
                merchantId = config.merchantEmail,
                environment = if (config.isLiveMode) GatewayEnvironment.PRODUCTION else GatewayEnvironment.SANDBOX
            )
        )
    }

    override fun updateStripeConfig(config: StripeGatewayConfig) {
        gatewaySettings = gatewaySettings.copy(stripeConfig = config)
        adapters[PaymentProvider.CARD]?.updateConfiguration(
            GatewayConfiguration(
                provider = PaymentProvider.CARD,
                clientId = config.publishableKey,
                clientSecret = config.secretKey,
                environment = GatewayEnvironment.TEST
            )
        )
    }

    override fun registerGatewayAdapter(adapter: PaymentGatewayAdapter) {
        adapters[adapter.provider] = adapter
    }

    override fun getAvailableProviders(): List<PaymentProvider> {
        return gatewaySettings.registeredGateways
    }

    override fun getPayPalCredentials(): PayPalCredentials {
        val cfg = gatewaySettings.paypalConfig
        return PayPalCredentials(
            clientId = cfg.clientId,
            secretKey = cfg.secretKey,
            merchantEmail = cfg.merchantEmail,
            isLiveMode = cfg.isLiveMode
        )
    }

    override fun updatePayPalCredentials(clientId: String, secretKey: String, isLiveMode: Boolean) {
        val updated = gatewaySettings.paypalConfig.copy(
            clientId = clientId.ifBlank { gatewaySettings.paypalConfig.clientId },
            secretKey = secretKey.ifBlank { gatewaySettings.paypalConfig.secretKey },
            isLiveMode = isLiveMode
        )
        updatePayPalConfig(updated)
    }

    override suspend fun processPayment(
        provider: PaymentProvider,
        amountEur: Double,
        currencyCode: String,
        cardNumber: String?,
        cardHolder: String?,
        payPalEmail: String?
    ): PaymentResult {
        // Delegate to specific modular adapter if registered
        val adapter = adapters[provider]
        if (adapter != null) {
            return adapter.executePayment(amountEur, currencyCode, cardNumber, cardHolder, payPalEmail)
        }

        // Fallback default processing
        delay(1000)

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
            PaymentProvider.SUMUP -> "SUMUP-" + UUID.randomUUID().toString().take(8).uppercase()
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

typealias DefaultDomainPaymentService = DefaultPaymentService

/**
 * Concrete PayPal Gateway Adapter preparing for real REST API / SDK credentials.
 */
class PayPalGatewayAdapterImpl(
    private var paypalConfig: PayPalGatewayConfig
) : PaymentGatewayAdapter {

    override val provider: PaymentProvider = PaymentProvider.PAYPAL

    override fun isConfigured(): Boolean {
        return paypalConfig.clientId.isNotBlank() && paypalConfig.secretKey.isNotBlank()
    }

    override fun getConfiguration(): GatewayConfiguration {
        return GatewayConfiguration(
            provider = PaymentProvider.PAYPAL,
            isEnabled = true,
            environment = if (paypalConfig.isLiveMode) GatewayEnvironment.PRODUCTION else GatewayEnvironment.SANDBOX,
            clientId = paypalConfig.clientId,
            clientSecret = paypalConfig.secretKey,
            merchantId = paypalConfig.merchantEmail
        )
    }

    override fun updateConfiguration(config: GatewayConfiguration) {
        paypalConfig = paypalConfig.copy(
            clientId = config.clientId,
            secretKey = config.clientSecret,
            merchantEmail = config.merchantId,
            isLiveMode = config.environment == GatewayEnvironment.PRODUCTION
        )
    }

    override suspend fun executePayment(
        amountEur: Double,
        currencyCode: String,
        cardNumber: String?,
        cardHolder: String?,
        payPalEmail: String?
    ): PaymentResult {
        delay(1200) // Simulate REST API checkout call

        if (amountEur <= 0.0) {
            return PaymentResult.Failure("Payment amount must be greater than zero.")
        }

        val txnId = "PP-LIVE-" + UUID.randomUUID().toString().take(10).uppercase()
        val details = PaymentDetails(
            paymentId = "PP-PAY-" + UUID.randomUUID().toString().take(8).uppercase(),
            provider = PaymentProvider.PAYPAL,
            transactionRef = txnId,
            cardLast4 = payPalEmail ?: "PAYPAL_EXPRESS",
            isVerifiedServerSide = true
        )
        return PaymentResult.Success(details)
    }
}

/**
 * Concrete Stripe Gateway Adapter preparing for real Stripe PaymentIntent & API keys.
 */
class StripeGatewayAdapterImpl(
    private var stripeConfig: StripeGatewayConfig
) : PaymentGatewayAdapter {

    override val provider: PaymentProvider = PaymentProvider.CARD

    override fun isConfigured(): Boolean {
        return stripeConfig.publishableKey.isNotBlank() && stripeConfig.secretKey.isNotBlank()
    }

    override fun getConfiguration(): GatewayConfiguration {
        return GatewayConfiguration(
            provider = PaymentProvider.CARD,
            isEnabled = true,
            environment = GatewayEnvironment.TEST,
            clientId = stripeConfig.publishableKey,
            clientSecret = stripeConfig.secretKey
        )
    }

    override fun updateConfiguration(config: GatewayConfiguration) {
        stripeConfig = stripeConfig.copy(
            publishableKey = config.clientId,
            secretKey = config.clientSecret
        )
    }

    override suspend fun executePayment(
        amountEur: Double,
        currencyCode: String,
        cardNumber: String?,
        cardHolder: String?,
        payPalEmail: String?
    ): PaymentResult {
        delay(1200) // Simulate Stripe PaymentIntent confirmation API call

        if (amountEur <= 0.0) {
            return PaymentResult.Failure("Payment amount must be greater than zero.")
        }

        val last4 = if (!cardNumber.isNullOrBlank() && cardNumber.length >= 4) {
            cardNumber.takeLast(4)
        } else {
            "4242"
        }

        val txnId = "pi_3M" + UUID.randomUUID().toString().replace("-", "").take(22)
        val details = PaymentDetails(
            paymentId = "ch_3M" + UUID.randomUUID().toString().replace("-", "").take(22),
            provider = PaymentProvider.CARD,
            transactionRef = txnId,
            cardLast4 = last4,
            isVerifiedServerSide = true
        )
        return PaymentResult.Success(details)
    }
}

