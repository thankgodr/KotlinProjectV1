package me.thankgodr.fintechchallegeapp.di

import me.thankgodr.fintechchallegeapp.data.remote.FirestoreDataSource
import me.thankgodr.fintechchallegeapp.data.remote.HttpClientFactory
import me.thankgodr.fintechchallegeapp.data.remote.PaymentApiService
import me.thankgodr.fintechchallegeapp.data.repository.PaymentRepositoryImpl
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import me.thankgodr.fintechchallegeapp.domain.usecase.ObserveTransactionsUseCase
import me.thankgodr.fintechchallegeapp.domain.usecase.SendPaymentUseCase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module as KoinModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.*

@KoinModule
@ComponentScan("me.thankgodr.fintechchallegeapp")
class AppModule

val dataModule = module {
    single { HttpClientFactory.create() }
    single { PaymentApiService(get()) }
    single { FirestoreDataSource() }
    single<PaymentRepository> { PaymentRepositoryImpl(get(), get()) }
    factory { SendPaymentUseCase(get()) }
    factory { ObserveTransactionsUseCase(get()) }
}

fun initKoin(additionalModules: List<Module> = emptyList()) {
    startKoin {
        modules(additionalModules + dataModule + AppModule().module)
    }
}
