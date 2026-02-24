package me.thankgodr.fintechchallegeapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionHistoryBddTest {
    private fun tx(
        id: String,
        email: String,
        amount: Double,
        status: TransactionStatus = TransactionStatus.COMPLETED,
    ) = Transaction(
        id = id,
        recipientEmail = email,
        amount = amount,
        currency = Currency.USD,
        senderName = "Tester",
        status = status,
        timestamp = "2026-02-24",
    )

    private class FakeRepository(
        private val transactionsFlow: Flow<List<Transaction>>,
    ) : PaymentRepository {
        override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> = Result.failure(NotImplementedError())

        override fun observeTransactions(): Flow<List<Transaction>> = transactionsFlow
    }

    // ── Scenario: User opens history with existing transactions ──

    @Test
    fun givenTransactionsExist_whenObserving_thenTransactionsAreEmitted() =
        runTest {
            val existing =
                listOf(
                    tx("tx-1", "alice@test.com", 50.0),
                    tx("tx-2", "bob@test.com", 120.0),
                )
            val repo = FakeRepository(flowOf(existing))
            val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val useCase = ObserveTransactionsUseCase(repo, testScope)

            val result = useCase().first()

            assertEquals(2, result.size)
            assertEquals("tx-1", result[0].id)
            assertEquals("tx-2", result[1].id)
        }

    // ── Scenario: User opens history with no transactions ──

    @Test
    fun givenNoTransactions_whenObserving_thenEmptyListIsEmitted() =
        runTest {
            val repo = FakeRepository(flowOf(emptyList()))
            val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val useCase = ObserveTransactionsUseCase(repo, testScope)

            val result = useCase().first()

            assertTrue(result.isEmpty())
        }

    // ── Scenario: New transaction arrives in real-time ──

    @Test
    fun givenObserving_whenNewTransactionArrives_thenListUpdates() =
        runTest {
            val flow = MutableSharedFlow<List<Transaction>>(replay = 1)
            val repo = FakeRepository(flow)
            val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val useCase = ObserveTransactionsUseCase(repo, testScope)

            val stateFlow = useCase()

            // Start collecting to activate WhileSubscribed
            val values = mutableListOf<List<Transaction>>()
            val job =
                testScope.backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    stateFlow.collect { values.add(it) }
                }

            // Initial emission
            flow.emit(listOf(tx("tx-1", "alice@test.com", 50.0)))
            testScope.testScheduler.advanceUntilIdle()
            assertEquals(1, stateFlow.value.size)

            // Real-time update with new transaction
            flow.emit(
                listOf(
                    tx("tx-1", "alice@test.com", 50.0),
                    tx("tx-2", "bob@test.com", 200.0),
                ),
            )
            testScope.testScheduler.advanceUntilIdle()
            assertEquals(2, stateFlow.value.size)
            assertEquals("tx-2", stateFlow.value[1].id)

            job.cancel()
        }

    // ── Scenario: Transactions contain mixed statuses ──

    @Test
    fun givenMixedStatuses_whenObserving_thenAllStatusesArePresent() =
        runTest {
            val transactions =
                listOf(
                    tx("tx-1", "a@test.com", 10.0, TransactionStatus.COMPLETED),
                    tx("tx-2", "b@test.com", 20.0, TransactionStatus.PENDING),
                    tx("tx-3", "c@test.com", 30.0, TransactionStatus.FAILED),
                )
            val repo = FakeRepository(flowOf(transactions))
            val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val useCase = ObserveTransactionsUseCase(repo, testScope)

            val result = useCase().first()

            assertEquals(3, result.size)
            assertEquals(TransactionStatus.COMPLETED, result[0].status)
            assertEquals(TransactionStatus.PENDING, result[1].status)
            assertEquals(TransactionStatus.FAILED, result[2].status)
        }

    // ── Scenario: Transaction amounts and currencies are preserved ──

    @Test
    fun givenTransactions_whenObserving_thenAmountsAndDetailsAreCorrect() =
        runTest {
            val transactions =
                listOf(
                    Transaction(
                        id = "tx-eur",
                        recipientEmail = "eu@bank.com",
                        amount = 999.99,
                        currency = Currency.EUR,
                        senderName = "EuroSender",
                        status = TransactionStatus.COMPLETED,
                        timestamp = "2026-02-24T10:00:00Z",
                    ),
                )
            val repo = FakeRepository(flowOf(transactions))
            val testScope = TestScope(UnconfinedTestDispatcher(testScheduler))
            val useCase = ObserveTransactionsUseCase(repo, testScope)

            val result = useCase().first()

            assertEquals(1, result.size)
            assertEquals(999.99, result[0].amount)
            assertEquals(Currency.EUR, result[0].currency)
            assertEquals("EuroSender", result[0].senderName)
            assertEquals("eu@bank.com", result[0].recipientEmail)
        }
}
