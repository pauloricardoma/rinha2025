package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentProcessorHealth(
    val default: PaymentProcessorHealthResponse,
    val fallback: PaymentProcessorHealthResponse
)
