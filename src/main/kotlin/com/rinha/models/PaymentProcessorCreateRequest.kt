package com.rinha.models

data class PaymentProcessorCreateRequest(
    val correlationId: String,
    val amount: Double,
    val type: PaymentType
)
