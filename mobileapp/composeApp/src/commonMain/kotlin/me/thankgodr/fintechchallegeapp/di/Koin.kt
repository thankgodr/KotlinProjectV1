package me.thankgodr.fintechchallegeapp.di

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import me.thankgodr.fintechchallegeapp.data.remote.HttpClientFactory
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module


@Single
class AppCoroutineScope : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Single
class HttpClientProvider {
    val client: HttpClient = HttpClientFactory.create()
}

@Module
@ComponentScan("me.thankgodr.fintechchallegeapp")
class SharedModule


fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(SharedModule().module)
    }
}
