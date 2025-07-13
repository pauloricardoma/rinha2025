package com.rinha.services

import com.rinha.models.Payment
import com.rinha.models.PaymentType
import com.rinha.repositories.PaymentsRepository
import io.ktor.server.application.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object PaymentWorker {
    fun start(
        app: Application,
        paymentsRepository: PaymentsRepository
    ) {
        app.launch {
            while (true) {
                val healthProcessors = PaymentProcessorHealthMonitor.get()

                if (PaymentQueue.queue.isNotEmpty()) {
                    val paymentRequest = PaymentQueue.queue.poll()
                    val processorDiffTime = healthProcessors.default.minResponseTime / healthProcessors.fallback.minResponseTime
                    val defaultIsBetter = processorDiffTime <= 3

                    val type = if (healthProcessors.default.failing || !defaultIsBetter) {
                        PaymentType.FALLBACK
                    } else {
                        PaymentType.DEFAULT
                    }

                    val payment = Payment(
                        correlationId = paymentRequest.correlationId,
                        amount = paymentRequest.amount,
                        type = type
                    )
                    val paymentResponse = paymentsRepository.create(payment)

                    if (!paymentResponse) {
                        PaymentQueue.queue.add(paymentRequest)
                    }
                } else {
                    delay(100)
                }
            }
        }
    }
}