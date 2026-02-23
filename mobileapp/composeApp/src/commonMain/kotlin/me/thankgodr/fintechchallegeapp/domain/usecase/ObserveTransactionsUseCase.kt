package me.thankgodr.fintechchallegeapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository

class ObserveTransactionsUseCase(
    private val repository: PaymentRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.observeTransactions()
    }
}
