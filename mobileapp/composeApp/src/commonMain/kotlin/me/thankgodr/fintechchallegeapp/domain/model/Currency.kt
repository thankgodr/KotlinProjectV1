package me.thankgodr.fintechchallegeapp.domain.model

data class Currency(
    val name: String,
    val countryCode: String,
    val currencyCode: String,
    val currencySymbol: String,
    val flagEmoji: String
) {
    companion object {
        val USD = Currency(
            name = "US Dollar",
            countryCode = "US",
            currencyCode = "USD",
            currencySymbol = "$",
            flagEmoji = "🇺🇸"
        )

        val EUR = Currency(
            name = "Euro",
            countryCode = "EU",
            currencyCode = "EUR",
            currencySymbol = "€",
            flagEmoji = "🇪🇺"
        )

        val supportedCurrencies = listOf(USD, EUR)

        fun fromCode(code: String): Currency? {
            return supportedCurrencies.firstOrNull { it.currencyCode.equals(code, ignoreCase = true) }
        }
    }
}
