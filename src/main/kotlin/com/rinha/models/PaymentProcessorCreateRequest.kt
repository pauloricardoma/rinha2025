package com.rinha.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class PaymentProcessorCreateRequest @OptIn(ExperimentalTime::class) constructor(
    val correlationId: String,
    val amount: Double,
    val type: PaymentType,
    val requestedAt: Instant
)
