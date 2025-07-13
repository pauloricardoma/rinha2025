package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val correlationId: String,
    val amount: Double,
    // Test with Int
    val type: PaymentType
)