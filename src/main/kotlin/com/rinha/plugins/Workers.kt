package com.rinha.plugins

import com.rinha.repositories.PaymentsRepository
import com.rinha.services.PaymentProcessorHealthMonitor
import com.rinha.services.PaymentProcessorService
import com.rinha.services.PaymentWorker
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

fun Application.configureWorkers() {
    val paymentsRepository by inject<PaymentsRepository>()
    PaymentWorker.start(this, paymentsRepository)

    val paymentService by inject<PaymentProcessorService>()
    PaymentProcessorHealthMonitor.start(this, paymentService)
}