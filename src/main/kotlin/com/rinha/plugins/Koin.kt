package com.rinha.plugins

import com.rinha.repositories.PaymentsRepository
import com.rinha.services.PaymentProcessorService
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.HttpClient
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin()  {
    install(Koin) {
        slf4jLogger()
        modules(appModule(environment))
    }
}

fun appModule(environment: ApplicationEnvironment) = module {
    single<HikariDataSource> { configureDatabases(environment) }
    single<HttpClient> { configureHttpClient() }
    single<PaymentsRepository> { PaymentsRepository() }
    single<PaymentProcessorService> { PaymentProcessorService(environment) }
}