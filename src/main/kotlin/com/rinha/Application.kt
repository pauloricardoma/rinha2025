package com.rinha

import com.rinha.plugins.configureKoin
import com.rinha.plugins.configureRouting
import com.rinha.plugins.configureWorkers
import com.rinha.repositories.PaymentsRepository
import com.rinha.services.PaymentProcessorHealthMonitor
import com.rinha.services.PaymentProcessorService
import com.rinha.services.PaymentWorker
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureWorkers()
    configureKoin()
}
