package me.thankgodr.fintechchallegeapp.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import me.thankgodr.fintechchallegeapp.data.mapper.toDomain
import me.thankgodr.fintechchallegeapp.data.mapper.toDto
import me.thankgodr.fintechchallegeapp.data.models.ApiResponseDto
import me.thankgodr.fintechchallegeapp.data.models.PaymentRequestDto
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentRepositoryImplTest {

    private val successDto = TransactionDto(
        id = "tx-repo-1",
        recipientEmail = "user@test.com",
        amount = 100.0,
        currency = "USD",
        senderName = "Alice",
        status = "COMPLETED",
        timestamp = "2026-01-01"
    )

    private val validRequest = PaymentRequest(
        recipientEmail = "user@test.com",
        amount = 100.0,
        currency = Currency.USD,
        senderName = "Alice"
    )

    /**
     * Testable repository that mirrors PaymentRepositoryImpl logic
     * but uses fakes instead of concrete platform-dependent classes.
     */
    private class TestableRepository(
        private val apiResult: ApiResponseDto<TransactionDto>? = null,
        private val apiException: Exception? = null,
        private val firestoreShouldThrow: Boolean = false,
        private val firestoreTransactions: List<TransactionDto> = emptyList(),
        private val scope: TestScope
    ) : PaymentRepository {

        val savedToFirestore = mutableListOf<TransactionDto>()

        override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> {
            return try {
                if (apiException != null) throw apiException
                val response = apiResult!!
                if (response.success && response.data != null) {
                    val transaction = response.data.toDomain()
                    scope.launch {
                        try {
                            if (firestoreShouldThrow) throw RuntimeException("Firestore error")
                            savedToFirestore.add(response.data)
                        } catch (_: Exception) { }
                    }
                    Result.success(transaction)
                } else {
                    Result.failure(
                        Exception(response.errors?.joinToString(", ") ?: "Unknown error")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override fun observeTransactions(): Flow<List<Transaction>> {
            return flowOf(firestoreTransactions).map { dtos -> dtos.map { it.toDomain() } }
        }
    }

    // ── sendPayment ──

    @Test
    fun sendPayment_successResponse_returnsDomainTransaction() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repo = TestableRepository(
            apiResult = ApiResponseDto(success = true, data = successDto),
            scope = testScope
        )

        val result = repo.sendPayment(validRequest)

        assertTrue(result.isSuccess)
        val tx = result.getOrNull()!!
        assertEquals("tx-repo-1", tx.id)
        assertEquals("user@test.com", tx.recipientEmail)
        assertEquals(100.0, tx.amount)
        assertEquals(Currency.USD, tx.currency)
        assertEquals(TransactionStatus.COMPLETED, tx.status)
    }

    @Test
    fun sendPayment_successResponse_savesToFirestore() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repo = TestableRepository(
            apiResult = ApiResponseDto(success = true, data = successDto),
            scope = testScope
        )

        repo.sendPayment(validRequest)
        testScope.advanceUntilIdle()

        assertEquals(1, repo.savedToFirestore.size)
        assertEquals("tx-repo-1", repo.savedToFirestore[0].id)
    }

    @Test
    fun sendPayment_firestoreFails_stillReturnsSuccess() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repo = TestableRepository(
            apiResult = ApiResponseDto(success = true, data = successDto),
            firestoreShouldThrow = true,
            scope = testScope
        )

        val result = repo.sendPayment(validRequest)
        testScope.advanceUntilIdle()

        assertTrue(result.isSuccess)
    }

    @Test
    fun sendPayment_apiReturnsErrors_returnsFailure() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repo = TestableRepository(
            apiResult = ApiResponseDto(success = false, errors = listOf("Invalid amount")),
            scope = testScope
        )

        val result = repo.sendPayment(validRequest)

        assertTrue(result.isFailure)
        assertEquals("Invalid amount", result.exceptionOrNull()?.message)
    }

    @Test
    fun sendPayment_apiThrowsException_returnsFailure() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repo = TestableRepository(
            apiException = RuntimeException("Connection refused"),
            scope = testScope
        )

        val result = repo.sendPayment(validRequest)

        assertTrue(result.isFailure)
        assertEquals("Connection refused", result.exceptionOrNull()?.message)
    }

    @Test
    fun sendPayment_apiReturnsNullData_returnsFailure() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repo = TestableRepository(
            apiResult = ApiResponseDto(success = true, data = null),
            scope = testScope
        )

        val result = repo.sendPayment(validRequest)

        assertTrue(result.isFailure)
    }

    // ── observeTransactions ──

    @Test
    fun observeTransactions_mapsDtosToDomain() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val dtos = listOf(
            successDto,
            TransactionDto("tx-2", "bob@t.com", 50.0, "EUR", "Bob", "PENDING", "2026-02-01")
        )
        val repo = TestableRepository(
            apiResult = ApiResponseDto(success = true, data = successDto),
            firestoreTransactions = dtos,
            scope = testScope
        )

        val transactions = repo.observeTransactions().first()

        assertEquals(2, transactions.size)
        assertEquals("tx-repo-1", transactions[0].id)
        assertEquals(Currency.USD, transactions[0].currency)
        assertEquals("tx-2", transactions[1].id)
        assertEquals(Currency.EUR, transactions[1].currency)
        assertEquals(TransactionStatus.PENDING, transactions[1].status)
    }
}
