package com.rinha.models

import com.rinha.utils.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class PaymentProcessorCreate @OptIn(ExperimentalTime::class) constructor(
    val correlationId: String,
    val amount: Double,
    @Serializable(InstantSerializer::class)
    val requestedAt: Instant
)
