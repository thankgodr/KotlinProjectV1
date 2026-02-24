package me.thankgodr.fintechchallegeapp.presentation.preview

import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentState
import me.thankgodr.fintechchallegeapp.presentation.transactionhistory.TransactionHistoryState

// ─── Sample Data ───

internal object PreviewData {
    val sampleTransactions =
        listOf(
            Transaction(
                id = "tx-001",
                recipientEmail = "alice@example.com",
                amount = 250.00,
                currency = Currency.USD,
                senderName = "John Doe",
                status = TransactionStatus.COMPLETED,
                timestamp = "2026-02-24T12:00:00Z",
            ),
            Transaction(
                id = "tx-002",
                recipientEmail = "bob@company.org",
                amount = 1500.50,
                currency = Currency.EUR,
                senderName = "Jane Smith",
                status = TransactionStatus.PENDING,
                timestamp = "2026-02-24T11:30:00Z",
            ),
            Transaction(
                id = "tx-003",
                recipientEmail = "charlie@bank.com",
                amount = 75.99,
                currency = Currency.USD,
                senderName = "John Doe",
                status = TransactionStatus.FAILED,
                timestamp = "2026-02-24T10:00:00Z",
            ),
        )

    val emptyPaymentState = SendPaymentState()

    val filledPaymentState =
        SendPaymentState(
            recipientEmail = "alice@example.com",
            amount = "250.00",
            senderName = "John Doe",
            selectedCurrency = Currency.USD,
            isFormValid = true,
        )

    val paymentStateWithErrors =
        SendPaymentState(
            recipientEmail = "bad-email",
            amount = "",
            senderName = "",
            emailError = "Invalid email address",
            amountError = "Amount must be greater than 0",
            senderNameError = "Sender name is required",
        )

    val loadingPaymentState =
        SendPaymentState(
            recipientEmail = "alice@example.com",
            amount = "100.00",
            senderName = "John",
            isLoading = true,
            isFormValid = true,
        )

    val transactionHistoryState =
        TransactionHistoryState(
            transactions = sampleTransactions,
            isLoading = false,
        )

    val emptyHistoryState =
        TransactionHistoryState(
            transactions = emptyList(),
            isLoading = false,
        )

    val loadingHistoryState = TransactionHistoryState(isLoading = true)

    val errorHistoryState =
        TransactionHistoryState(
            isLoading = false,
            error = "Failed to load transactions",
        )
}
