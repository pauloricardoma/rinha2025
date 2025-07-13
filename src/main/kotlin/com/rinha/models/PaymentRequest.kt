package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val correlationId: String,
    val amount: Double,
)