package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentSummary(
    val default: TotalPayment,
    val fallback: TotalPayment
)