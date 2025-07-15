package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentProcessorHealthResponse(
    var failing: Boolean,
    var minResponseTime: Int?
)
