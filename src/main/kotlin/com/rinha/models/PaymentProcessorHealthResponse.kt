package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentProcessorHealthResponse(
    val failing: Boolean,
    val minResponseTime: Int
)
