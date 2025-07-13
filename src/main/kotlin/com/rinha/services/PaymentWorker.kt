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
                    val payment = Payment(
                        correlationId = paymentRequest.correlationId,
                        amount = paymentRequest.amount,
                        type = PaymentType.DEFAULT
                    )
                    paymentsRepository.create(payment)
                } else {
                    delay(100)
                }
            }
        }
    }
}