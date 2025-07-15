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
        minResponseTime = null
    )
    private var fallbackServiceHealth = PaymentProcessorHealthResponse(
        failing = false,
        minResponseTime = null
    )

    fun start(app: Application, paymentService: PaymentProcessorService) {
        app.launch {
            while (true) {
                val responseDefault = paymentService.serviceHealth(PaymentType.DEFAULT)
                val responseFallback = paymentService.serviceHealth(PaymentType.FALLBACK)

                if (responseDefault != null) {
                    defaultServiceHealth.failing = responseDefault.failing
                    defaultServiceHealth.minResponseTime = responseDefault.minResponseTime
                }
                if (responseFallback != null) {
                    fallbackServiceHealth.failing = responseFallback.failing
                    fallbackServiceHealth.minResponseTime = responseFallback.minResponseTime
                }

                delay(5000)
            }
        }
    }

    fun get(): PaymentProcessorHealth {
        return PaymentProcessorHealth(
            default = defaultServiceHealth,
            fallback = fallbackServiceHealth
        )
    }
}