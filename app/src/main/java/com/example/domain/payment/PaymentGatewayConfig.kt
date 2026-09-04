package com.example.domain.payment

import com.example.data.model.PaymentProvider

enum class GatewayEnvironment {
    SANDBOX,
    TEST,
    PRODUCTION
}

data class GatewayConfiguration(
    val provider: PaymentProvider,
    val isEnabled: Boolean = true,
    val environment: GatewayEnvironment = GatewayEnvironment.SANDBOX,
    val clientId: String = "",
    val clientSecret: String = "",
    val merchantId: String = "",
    val webhookUrl: String = "",
    val customSettings: Map<String, String> = emptyMap()
)

data class PayPalGatewayConfig(
    val clientId: String = "sb-client-id-tuk24-lisbon-live-sandbox",
    val secretKey: String = "sb-secret-key-tuk24-express-pass",
    val merchantEmail: String = "paypal-checkout@tuktuk24.pt",
    val isLiveMode: Boolean = false,
    val allowExpressQuickPay: Boolean = true
)

data class StripeGatewayConfig(
    val publishableKey: String = "pk_test_tuk24_lisbon_express",
    val secretKey: String = "sk_test_tuk24_lisbon_express",
    val enableApplePay: Boolean = true,
    val enableGooglePay: Boolean = true
)

data class ModularPaymentGatewaySettings(
    val paypalConfig: PayPalGatewayConfig = PayPalGatewayConfig(),
    val stripeConfig: StripeGatewayConfig = StripeGatewayConfig(),
    val activeEnvironment: GatewayEnvironment = GatewayEnvironment.SANDBOX,
    val registeredGateways: List<PaymentProvider> = listOf(
        PaymentProvider.PAYPAL,
        PaymentProvider.CARD,
        PaymentProvider.APPLE_PAY,
        PaymentProvider.GOOGLE_PAY,
        PaymentProvider.SUMUP,
        PaymentProvider.CASH_ON_ARRIVAL
    )
)
