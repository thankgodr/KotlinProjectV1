package me.thankgodr.fintechchallegeapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import me.thankgodr.fintechchallegeapp.domain.validation.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SendPaymentUseCaseTest {
    private val successTransaction =
        Transaction(
            id = "tx-1",
            recipientEmail = "user@test.com",
            amount = 100.0,
            currency = Currency.USD,
            senderName = "Alice",
            status = TransactionStatus.COMPLETED,
            timestamp = "2026-01-01",
        )

    private class FakeRepository(
        private val result: Result<Transaction>,
    ) : PaymentRepository {
        var lastRequest: PaymentRequest? = null

        override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> {
            lastRequest = request
            return result
        }

        override fun observeTransactions(): Flow<List<Transaction>> = flowOf(emptyList())
    }

    @Test
    fun validPayment_callsRepositoryAndReturnsSuccess() =
        runTest {
            val repo = FakeRepository(Result.success(successTransaction))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("user@test.com", 100.0, "USD", "Alice")

            assertTrue(result.isSuccess)
            assertEquals(successTransaction, result.getOrNull())
            assertEquals("user@test.com", repo.lastRequest?.recipientEmail)
            assertEquals(100.0, repo.lastRequest?.amount)
            assertEquals(Currency.USD, repo.lastRequest?.currency)
        }

    @Test
    fun invalidEmail_returnsValidationFailure_doesNotCallRepository() =
        runTest {
            val repo = FakeRepository(Result.success(successTransaction))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("bad-email", 100.0, "USD", "Alice")

            assertTrue(result.isFailure)
            assertIs<ValidationException>(result.exceptionOrNull())
            val errors = (result.exceptionOrNull() as ValidationException).errors
            assertTrue(errors.any { it is ValidationError.InvalidEmail })
            // Repository should NOT have been called
            assertEquals(null, repo.lastRequest)
        }

    @Test
    fun zeroAmount_returnsValidationFailure() =
        runTest {
            val repo = FakeRepository(Result.success(successTransaction))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("user@test.com", 0.0, "USD", "Alice")

            assertTrue(result.isFailure)
            val errors = (result.exceptionOrNull() as ValidationException).errors
            assertTrue(errors.any { it is ValidationError.InvalidAmount })
        }

    @Test
    fun emptySenderName_returnsValidationFailure() =
        runTest {
            val repo = FakeRepository(Result.success(successTransaction))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("user@test.com", 50.0, "USD", "")

            assertTrue(result.isFailure)
            val errors = (result.exceptionOrNull() as ValidationException).errors
            assertTrue(errors.any { it is ValidationError.EmptySenderName })
        }

    @Test
    fun unsupportedCurrency_returnsValidationFailure() =
        runTest {
            val repo = FakeRepository(Result.success(successTransaction))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("user@test.com", 50.0, "GBP", "Alice")

            assertTrue(result.isFailure)
            val errors = (result.exceptionOrNull() as ValidationException).errors
            assertTrue(errors.any { it is ValidationError.InvalidCurrency })
        }

    @Test
    fun multipleValidationErrors_returnsAll() =
        runTest {
            val repo = FakeRepository(Result.success(successTransaction))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("", -1.0, "XYZ", "")

            assertTrue(result.isFailure)
            val errors = (result.exceptionOrNull() as ValidationException).errors
            assertEquals(4, errors.size)
        }

    @Test
    fun repositoryFailure_propagatesError() =
        runTest {
            val repo = FakeRepository(Result.failure(RuntimeException("Network error")))
            val useCase = SendPaymentUseCase(repo)

            val result = useCase("user@test.com", 50.0, "USD", "Alice")

            assertTrue(result.isFailure)
            assertEquals("Network error", result.exceptionOrNull()?.message)
        }
}
