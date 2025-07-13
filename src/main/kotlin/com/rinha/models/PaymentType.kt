package com.rinha.models

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentType(val value: Int) {
    DEFAULT(0),
    FALLBACK(1)
}