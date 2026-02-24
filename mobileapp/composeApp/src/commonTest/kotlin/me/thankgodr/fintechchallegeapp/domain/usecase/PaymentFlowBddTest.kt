package me.thankgodr.fintechchallegeapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PaymentFlowBddTest {
    private val completedTransaction =
        Transaction(
            id = "tx-bdd-1",
            recipientEmail = "recipient@bank.com",
            amount = 250.0,
            currency = Currency.EUR,
            senderName = "Jane Doe",
            status = TransactionStatus.COMPLETED,
            timestamp = "2026-02-24T12:00:00Z",
        )

    private class FakeRepository(
        private val result: Result<Transaction>,
        private val transactions: List<Transaction> = emptyList(),
    ) : PaymentRepository {
        var sendPaymentCalled = false

        override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> {
            sendPaymentCalled = true
            return result
        }

        override fun observeTransactions(): Flow<List<Transaction>> = flowOf(transactions)
    }

    // ── Scenario: Successful payment ──

    @Test
    fun givenValidDetails_whenSendingPayment_thenPaymentSucceeds() =
        runTest {
            // GIVEN a repository that will accept the payment
            val repo = FakeRepository(Result.success(completedTransaction))
            val sendPayment = SendPaymentUseCase(repo)

            // WHEN user sends a valid payment
            val result =
                sendPayment(
                    recipientEmail = "recipient@bank.com",
                    amount = 250.0,
                    currencyCode = "EUR",
                    senderName = "Jane Doe",
                )

            // THEN payment succeeds with the expected transaction
            assertTrue(result.isSuccess)
            assertEquals("tx-bdd-1", result.getOrNull()?.id)
            assertEquals(250.0, result.getOrNull()?.amount)
            assertEquals(Currency.EUR, result.getOrNull()?.currency)
            assertTrue(repo.sendPaymentCalled)
        }

    // ── Scenario: Payment rejected due to invalid email ──

    @Test
    fun givenInvalidEmail_whenSendingPayment_thenPaymentIsRejected() =
        runTest {
            // GIVEN a repository (should never be called)
            val repo = FakeRepository(Result.success(completedTransaction))
            val sendPayment = SendPaymentUseCase(repo)

            // WHEN user sends payment with invalid email
            val result =
                sendPayment(
                    recipientEmail = "not-an-email",
                    amount = 100.0,
                    currencyCode = "USD",
                    senderName = "John",
                )

            // THEN payment fails with validation error
            assertTrue(result.isFailure)
            assertIs<ValidationException>(result.exceptionOrNull())
            // AND repository is never called
            assertTrue(!repo.sendPaymentCalled)
        }

    // ── Scenario: Payment rejected due to zero amount ──

    @Test
    fun givenZeroAmount_whenSendingPayment_thenPaymentIsRejected() =
        runTest {
            val repo = FakeRepository(Result.success(completedTransaction))
            val sendPayment = SendPaymentUseCase(repo)

            val result =
                sendPayment(
                    recipientEmail = "user@test.com",
                    amount = 0.0,
                    currencyCode = "USD",
                    senderName = "John",
                )

            assertTrue(result.isFailure)
            assertTrue(!repo.sendPaymentCalled)
        }

    // ── Scenario: Network failure during payment ──

    @Test
    fun givenNetworkError_whenSendingPayment_thenPaymentFails() =
        runTest {
            // GIVEN a repository that will fail
            val repo = FakeRepository(Result.failure(RuntimeException("Connection refused")))
            val sendPayment = SendPaymentUseCase(repo)

            // WHEN user sends a valid payment
            val result =
                sendPayment(
                    recipientEmail = "user@test.com",
                    amount = 50.0,
                    currencyCode = "USD",
                    senderName = "Alice",
                )

            // THEN payment fails with the network error
            assertTrue(result.isFailure)
            assertEquals("Connection refused", result.exceptionOrNull()?.message)
        }

    // ── Scenario: Payment with unsupported currency ──

    @Test
    fun givenUnsupportedCurrency_whenSendingPayment_thenPaymentIsRejected() =
        runTest {
            val repo = FakeRepository(Result.success(completedTransaction))
            val sendPayment = SendPaymentUseCase(repo)

            val result =
                sendPayment(
                    recipientEmail = "user@test.com",
                    amount = 50.0,
                    currencyCode = "JPY",
                    senderName = "Bob",
                )

            assertTrue(result.isFailure)
            assertTrue(!repo.sendPaymentCalled)
        }
}
