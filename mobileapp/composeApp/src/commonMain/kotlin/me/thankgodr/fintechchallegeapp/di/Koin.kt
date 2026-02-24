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
import org.koin.dsl.koinApplication


@Single
class AppCoroutineScope : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Single
class HttpClientProvider {
    val client: HttpClient = HttpClientFactory.create()
}

@Module
@ComponentScan("me.thankgodr.fintechchallegeapp")
class AppModule



fun initKoin() {
    startKoin{
        modules(AppModule().module)
    }
}
