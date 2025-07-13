package com.rinha.models

data class PaymentProcessorHealth(
    val default: PaymentProcessorHealthResponse,
    val fallback: PaymentProcessorHealthResponse
)
