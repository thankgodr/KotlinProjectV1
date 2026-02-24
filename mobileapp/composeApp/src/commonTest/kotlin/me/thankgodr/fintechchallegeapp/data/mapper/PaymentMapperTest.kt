package me.thankgodr.fintechchallegeapp.data.mapper

import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class PaymentMapperTest {
    @Test
    fun paymentRequestToDto_mapsCorrectly() {
        val request =
            PaymentRequest(
                recipientEmail = "test@email.com",
                amount = 99.99,
                currency = Currency.USD,
                senderName = "Alice",
            )

        val dto = request.toDto()

        assertEquals("test@email.com", dto.recipientEmail)
        assertEquals(99.99, dto.amount)
        assertEquals("USD", dto.currency)
        assertEquals("Alice", dto.senderName)
    }

    @Test
    fun transactionDtoToDomain_mapsCorrectly() {
        val dto =
            TransactionDto(
                id = "tx-123",
                recipientEmail = "bob@test.com",
                amount = 50.0,
                currency = "EUR",
                senderName = "Alice",
                status = "COMPLETED",
                timestamp = "2026-01-01T12:00:00Z",
            )

        val domain = dto.toDomain()

        assertEquals("tx-123", domain.id)
        assertEquals("bob@test.com", domain.recipientEmail)
        assertEquals(50.0, domain.amount)
        assertEquals(Currency.EUR, domain.currency)
        assertEquals("Alice", domain.senderName)
        assertEquals(TransactionStatus.COMPLETED, domain.status)
        assertEquals("2026-01-01T12:00:00Z", domain.timestamp)
    }

    @Test
    fun transactionDtoToDomain_unknownCurrency_defaultsToUSD() {
        val dto =
            TransactionDto(
                id = "tx-456",
                recipientEmail = "bob@test.com",
                amount = 25.0,
                currency = "GBP",
                senderName = "Charlie",
                status = "PENDING",
                timestamp = "2026-01-01",
            )

        val domain = dto.toDomain()

        assertEquals(Currency.USD, domain.currency)
    }

    @Test
    fun transactionDtoToDomain_unknownStatus_defaultsToPending() {
        val dto =
            TransactionDto(
                id = "tx-789",
                recipientEmail = "bob@test.com",
                amount = 10.0,
                currency = "USD",
                senderName = "Dave",
                status = "SOME_STATUS",
                timestamp = "2026-01-01",
            )

        val domain = dto.toDomain()

        assertEquals(TransactionStatus.PENDING, domain.status)
    }

    // ── Transaction.toDto() ──

    @Test
    fun transactionToDto_mapsCorrectly() {
        val transaction =
            me.thankgodr.fintechchallegeapp.domain.model.Transaction(
                id = "tx-abc",
                recipientEmail = "eve@test.com",
                amount = 75.0,
                currency = Currency.EUR,
                senderName = "Frank",
                status = TransactionStatus.FAILED,
                timestamp = "2026-02-01",
            )

        val dto = transaction.toDto()

        assertEquals("tx-abc", dto.id)
        assertEquals("eve@test.com", dto.recipientEmail)
        assertEquals(75.0, dto.amount)
        assertEquals("EUR", dto.currency)
        assertEquals("Frank", dto.senderName)
        assertEquals("failed", dto.status)
        assertEquals("2026-02-01", dto.timestamp)
    }
}
