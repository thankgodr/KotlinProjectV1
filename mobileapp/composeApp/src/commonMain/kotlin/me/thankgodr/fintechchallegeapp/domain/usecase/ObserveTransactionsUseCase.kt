package me.thankgodr.fintechchallegeapp.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository

class ObserveTransactionsUseCase(
    private val repository: PaymentRepository,
    private val applicationScope: CoroutineScope
) {

    private val shared: StateFlow<List<Transaction>> =
        repository.observeTransactions()
            .distinctUntilChanged()
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    operator fun invoke(): StateFlow<List<Transaction>> = shared
}
