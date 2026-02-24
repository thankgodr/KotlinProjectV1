package me.thankgodr.fintechchallegeapp.presentation.transactionhistory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import me.thankgodr.fintechchallegeapp.domain.usecase.ObserveTransactionsUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionHistoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun tx(id: String, email: String, amount: Double) = Transaction(
        id = id,
        recipientEmail = email,
        amount = amount,
        currency = Currency.USD,
        senderName = "Tester",
        status = TransactionStatus.COMPLETED,
        timestamp = "2026-01-01"
    )

    private class FakeRepository(
        private val flow: Flow<List<Transaction>>
    ) : PaymentRepository {
        override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> =
            Result.failure(NotImplementedError())
        override fun observeTransactions(): Flow<List<Transaction>> = flow
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(flow: Flow<List<Transaction>>): TransactionHistoryViewModel {
        val repo = FakeRepository(flow)
        val useCase = ObserveTransactionsUseCase(repo, testScope)
        return TransactionHistoryViewModel(useCase)
    }

    @Test
    fun initialLoad_emitsTransactions() = runTest {
        val transactions = listOf(tx("tx-1", "a@b.com", 50.0))
        val vm = createViewModel(flowOf(transactions))

        assertFalse(vm.state.value.isLoading)
        assertEquals(1, vm.state.value.transactions.size)
        assertEquals("tx-1", vm.state.value.transactions[0].id)
        assertNull(vm.state.value.error)
    }

    @Test
    fun emptyTransactions_showsEmptyList() = runTest {
        val vm = createViewModel(flowOf(emptyList()))

        assertFalse(vm.state.value.isLoading)
        assertTrue(vm.state.value.transactions.isEmpty())
    }

    @Test
    fun multipleTransactions_allPresent() = runTest {
        val list = listOf(
            tx("tx-1", "a@test.com", 10.0),
            tx("tx-2", "b@test.com", 20.0),
            tx("tx-3", "c@test.com", 30.0)
        )
        val vm = createViewModel(flowOf(list))

        assertEquals(3, vm.state.value.transactions.size)
    }

    @Test
    fun retry_reloadsTransactions() = runTest {
        val flow = MutableSharedFlow<List<Transaction>>(replay = 1)
        flow.emit(listOf(tx("tx-1", "a@b.com", 50.0)))
        val vm = createViewModel(flow)

        assertEquals(1, vm.state.value.transactions.size)

        // Emit new data and retry
        flow.emit(listOf(
            tx("tx-1", "a@b.com", 50.0),
            tx("tx-2", "c@d.com", 100.0)
        ))
        vm.onIntent(TransactionHistoryIntent.Retry)

        assertEquals(2, vm.state.value.transactions.size)
    }
}
