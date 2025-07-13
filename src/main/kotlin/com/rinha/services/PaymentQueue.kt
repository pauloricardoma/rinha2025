package com.rinha.services

import com.rinha.models.PaymentRequest
import java.util.concurrent.ConcurrentLinkedQueue

object PaymentQueue {
    val queue = ConcurrentLinkedQueue<PaymentRequest>()
}