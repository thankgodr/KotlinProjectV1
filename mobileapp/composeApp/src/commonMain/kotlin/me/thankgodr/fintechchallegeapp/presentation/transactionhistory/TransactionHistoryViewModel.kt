package me.thankgodr.fintechchallegeapp.presentation.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.thankgodr.fintechchallegeapp.domain.usecase.ObserveTransactionsUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TransactionHistoryViewModel(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionHistoryState())
    val state: StateFlow<TransactionHistoryState> = _state.asStateFlow()

    init {
        onIntent(TransactionHistoryIntent.LoadTransactions)
    }

    fun onIntent(intent: TransactionHistoryIntent) {
        when (intent) {
            is TransactionHistoryIntent.LoadTransactions -> observeTransactions()
            is TransactionHistoryIntent.Retry -> {
                reduce { copy(isLoading = true, error = null) }
                observeTransactions()
            }
        }
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            try {
                observeTransactionsUseCase()
                    .collect { transactions ->
                        reduce {
                            TransactionHistoryState(
                                transactions = transactions,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                reduce {
                    copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load transactions"
                    )
                }
            }
        }
    }

    private fun reduce(block: TransactionHistoryState.() -> TransactionHistoryState) {
        _state.value = _state.value.block()
    }
}
