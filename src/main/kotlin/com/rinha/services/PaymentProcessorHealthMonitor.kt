package com.rinha.services

import com.rinha.models.PaymentProcessorHealth
import com.rinha.models.PaymentProcessorHealthResponse
import com.rinha.models.PaymentType
import io.ktor.server.application.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object PaymentProcessorHealthMonitor {
    private var defaultServiceHealth = PaymentProcessorHealthResponse(
        failing = false,
        minResponseTime = 0
    )
    private var fallbackServiceHealth = PaymentProcessorHealthResponse(
        failing = false,
        minResponseTime = 0
    )

    fun start(app: Application, paymentService: PaymentProcessorService) {
        app.launch {
            while (true) {
                val responseDefault = paymentService.serviceHealth(PaymentType.DEFAULT)
                val responseFallback = paymentService.serviceHealth(PaymentType.FALLBACK)

                defaultServiceHealth = responseDefault ?: defaultServiceHealth
                fallbackServiceHealth = responseFallback ?: fallbackServiceHealth

                delay(5000)
            }
        }
    }

    fun get(): PaymentProcessorHealth {
        return PaymentProcessorHealth(
            defaultServiceHealth,
            fallback = fallbackServiceHealth
        )
    }
}