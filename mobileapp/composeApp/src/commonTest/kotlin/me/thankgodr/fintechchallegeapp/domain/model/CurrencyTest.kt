package me.thankgodr.fintechchallegeapp.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CurrencyTest {

    @Test
    fun usdHasCorrectProperties() {
        val usd = Currency.USD
        assertEquals("USD", usd.currencyCode)
        assertEquals("$", usd.currencySymbol)
        assertEquals("US Dollar", usd.name)
        assertEquals("US", usd.countryCode)
    }

    @Test
    fun eurHasCorrectProperties() {
        val eur = Currency.EUR
        assertEquals("EUR", eur.currencyCode)
        assertEquals("€", eur.currencySymbol)
        assertEquals("Euro", eur.name)
        assertEquals("EU", eur.countryCode)
    }

    @Test
    fun supportedCurrenciesContainsUsdAndEur() {
        assertEquals(2, Currency.supportedCurrencies.size)
        assertTrue(Currency.supportedCurrencies.contains(Currency.USD))
        assertTrue(Currency.supportedCurrencies.contains(Currency.EUR))
    }

    @Test
    fun fromCode_validUSD_returnsCurrency() {
        val result = Currency.fromCode("USD")
        assertNotNull(result)
        assertEquals(Currency.USD, result)
    }

    @Test
    fun fromCode_validEUR_returnsCurrency() {
        val result = Currency.fromCode("EUR")
        assertNotNull(result)
        assertEquals(Currency.EUR, result)
    }

    @Test
    fun fromCode_caseInsensitive() {
        assertNotNull(Currency.fromCode("usd"))
        assertNotNull(Currency.fromCode("Eur"))
    }

    @Test
    fun fromCode_unsupported_returnsNull() {
        assertNull(Currency.fromCode("GBP"))
        assertNull(Currency.fromCode("JPY"))
        assertNull(Currency.fromCode(""))
    }
}
