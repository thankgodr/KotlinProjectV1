package me.thankgodr.fintechchallegeapp.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module as KoinModule
import org.koin.ksp.generated.*

@KoinModule
@ComponentScan("me.thankgodr.fintechchallegeapp")
class AppModule

fun initKoin(additionalModules: List<Module> = emptyList()) {
    startKoin {
        modules(additionalModules + AppModule().module)
    }
}
