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

        val GBP = Currency(
            name = "British Pound",
            countryCode = "GB",
            currencyCode = "GBP",
            currencySymbol = "£",
            flagEmoji = "🇬🇧"
        )

        val NGN = Currency(
            name = "Nigerian Naira",
            countryCode = "NG",
            currencyCode = "NGN",
            currencySymbol = "₦",
            flagEmoji = "🇳🇬"
        )

        val GHS = Currency(
            name = "Ghanaian Cedi",
            countryCode = "GH",
            currencyCode = "GHS",
            currencySymbol = "GH₵",
            flagEmoji = "🇬🇭"
        )

        val supportedCurrencies = listOf(USD, EUR, GBP)

        fun fromCode(code: String): Currency? {
            return supportedCurrencies.firstOrNull { it.currencyCode.equals(code, ignoreCase = true) }
        }
    }
}
