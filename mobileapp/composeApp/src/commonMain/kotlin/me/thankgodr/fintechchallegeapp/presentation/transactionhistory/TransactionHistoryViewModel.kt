package me.thankgodr.fintechchallegeapp.presentation.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
            observeTransactionsUseCase()
                .catch { e ->
                    reduce {
                        copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load transactions"
                        )
                    }
                }
                .collect { transactions ->
                    reduce {
                        TransactionHistoryState(
                            transactions = transactions,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun reduce(block: TransactionHistoryState.() -> TransactionHistoryState) {
        _state.value = _state.value.block()
    }
}
