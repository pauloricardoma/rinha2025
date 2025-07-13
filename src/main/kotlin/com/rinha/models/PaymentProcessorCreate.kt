package com.rinha.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class PaymentProcessorCreate @OptIn(ExperimentalTime::class) constructor(
    val correlationId: String,
    val amount: Double,
    val requestedAt: Instant
)
