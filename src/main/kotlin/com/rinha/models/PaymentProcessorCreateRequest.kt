package com.rinha.models

import java.time.Instant

data class PaymentProcessorCreateRequest(
    val correlationId: String,
    val amount: Double,
    val type: PaymentType
)
