package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
data class TotalPayment(
    var totalRequests: Int,
    var totalAmount: Double
)
