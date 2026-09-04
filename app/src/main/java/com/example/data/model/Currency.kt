package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val exchangeRateFromEur: Double,
    val locale: Locale
) {
    EUR("EUR", "€", 1.0, Locale.GERMANY),
    USD("USD", "$", 1.09, Locale.US),
    GBP("GBP", "£", 0.86, Locale.UK),
    CHF("CHF", "CHF", 0.95, Locale.FRANCE),
    CAD("CAD", "CA$", 1.48, Locale.CANADA),
    AUD("AUD", "A$", 1.65, Locale.US);

    fun format(amountInEur: Double): String {
        val converted = amountInEur * exchangeRateFromEur
        return when (this) {
            EUR -> "€${String.format(Locale.US, "%.2f", converted)}"
            USD -> "$${String.format(Locale.US, "%.2f", converted)}"
            GBP -> "£${String.format(Locale.US, "%.2f", converted)}"
            CHF -> "${String.format(Locale.US, "%.2f", converted)} CHF"
            CAD -> "CA$${String.format(Locale.US, "%.2f", converted)}"
            AUD -> "A$${String.format(Locale.US, "%.2f", converted)}"
        }
    }

    companion object {
        fun fromCode(code: String): AppCurrency {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: EUR
        }
    }
}
