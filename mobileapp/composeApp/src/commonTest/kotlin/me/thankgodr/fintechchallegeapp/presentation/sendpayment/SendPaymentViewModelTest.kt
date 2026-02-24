package me.thankgodr.fintechchallegeapp.presentation.sendpayment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import me.thankgodr.fintechchallegeapp.domain.usecase.SendPaymentUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SendPaymentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val successTransaction = Transaction(
        id = "tx-vm-1",
        recipientEmail = "user@test.com",
        amount = 100.0,
        currency = Currency.USD,
        senderName = "Alice",
        status = TransactionStatus.COMPLETED,
        timestamp = "2026-01-01"
    )

    private class FakeRepository(
        private val result: Result<Transaction>
    ) : PaymentRepository {
        override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> = result
        override fun observeTransactions(): Flow<List<Transaction>> = flowOf(emptyList())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(result: Result<Transaction> = Result.success(successTransaction)): SendPaymentViewModel {
        val repo = FakeRepository(result)
        val useCase = SendPaymentUseCase(repo)
        return SendPaymentViewModel(useCase)
    }

    // ── Field Updates ──

    @Test
    fun updateRecipientEmail_updatesState() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("a@b.com"))
        assertEquals("a@b.com", vm.state.value.recipientEmail)
    }

    @Test
    fun updateAmount_updatesState() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateAmount("50.00"))
        assertEquals("50.00", vm.state.value.amount)
    }

    @Test
    fun updateSenderName_updatesState() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateSenderName("Bob"))
        assertEquals("Bob", vm.state.value.senderName)
    }

    @Test
    fun selectCurrency_updatesState() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.SelectCurrency(Currency.EUR))
        assertEquals(Currency.EUR, vm.state.value.selectedCurrency)
    }

    // ── isFormValid ──

    @Test
    fun allFieldsFilled_isFormValidTrue() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("a@b.com"))
        vm.onIntent(SendPaymentIntent.UpdateAmount("100"))
        vm.onIntent(SendPaymentIntent.UpdateSenderName("Alice"))
        assertTrue(vm.state.value.isFormValid)
    }

    @Test
    fun missingField_isFormValidFalse() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("a@b.com"))
        vm.onIntent(SendPaymentIntent.UpdateAmount("100"))
        // senderName is empty
        assertFalse(vm.state.value.isFormValid)
    }

    // ── Field Update Clears Errors ──

    @Test
    fun updateEmail_clearsEmailError() = runTest {
        val vm = createViewModel()
        // Submit with empty fields to trigger errors
        vm.onIntent(SendPaymentIntent.SubmitPayment)
        assertNotNull(vm.state.value.emailError)
        // Typing clears the error
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("a"))
        assertNull(vm.state.value.emailError)
    }

    // ── Submit Payment ──

    @Test
    fun submitWithEmptyFields_setsValidationErrors() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.SubmitPayment)
        assertNotNull(vm.state.value.emailError)
        assertNotNull(vm.state.value.amountError)
        assertNotNull(vm.state.value.senderNameError)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun submitWithValidFields_setsSuccessTransaction() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("user@test.com"))
        vm.onIntent(SendPaymentIntent.UpdateAmount("100"))
        vm.onIntent(SendPaymentIntent.UpdateSenderName("Alice"))
        vm.onIntent(SendPaymentIntent.SubmitPayment)

        assertFalse(vm.state.value.isLoading)
        assertNotNull(vm.state.value.successTransaction)
        assertEquals("tx-vm-1", vm.state.value.successTransaction?.id)
    }

    @Test
    fun submitWithValidFields_repositoryFails_setsGeneralError() = runTest {
        val vm = createViewModel(Result.failure(RuntimeException("Network error")))
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("user@test.com"))
        vm.onIntent(SendPaymentIntent.UpdateAmount("100"))
        vm.onIntent(SendPaymentIntent.UpdateSenderName("Alice"))
        vm.onIntent(SendPaymentIntent.SubmitPayment)

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.successTransaction)
        assertEquals("Network error", vm.state.value.generalError)
    }

    // ── Reset Form ──

    @Test
    fun resetForm_clearsAllState() = runTest {
        val vm = createViewModel()
        vm.onIntent(SendPaymentIntent.UpdateRecipientEmail("user@test.com"))
        vm.onIntent(SendPaymentIntent.UpdateAmount("50"))
        vm.onIntent(SendPaymentIntent.UpdateSenderName("Bob"))

        vm.onIntent(SendPaymentIntent.ResetForm)

        assertEquals("", vm.state.value.recipientEmail)
        assertEquals("", vm.state.value.amount)
        assertEquals("", vm.state.value.senderName)
        assertFalse(vm.state.value.isFormValid)
        assertNull(vm.state.value.successTransaction)
    }
}
