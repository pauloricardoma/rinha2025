package com.rinha.services

import com.rinha.models.PaymentProcessorCreate
import com.rinha.models.PaymentProcessorCreateRequest
import com.rinha.models.PaymentProcessorCreateResponse
import com.rinha.models.PaymentProcessorHealthResponse
import com.rinha.models.PaymentType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationEnvironment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PaymentProcessorService(environment: ApplicationEnvironment): KoinComponent {
    private val httpClient by inject<HttpClient>()

    val defaultUrl = environment.config.propertyOrNull("ktor.service.payment.processorDefaultUrl")?.getString()
    val fallbackUrl = environment.config.propertyOrNull("ktor.service.payment.processorFallbackUrl")?.getString()

    @OptIn(ExperimentalTime::class)
    suspend fun create(paymentProcessorCreateRequest: PaymentProcessorCreateRequest): String? {
        return try {
            val body = PaymentProcessorCreate(
                correlationId = paymentProcessorCreateRequest.correlationId,
                amount = paymentProcessorCreateRequest.amount,
                requestedAt = Clock.System.now()
            )

            var url: String = if (paymentProcessorCreateRequest.type == PaymentType.DEFAULT) {
                defaultUrl ?: ""
            } else {
                fallbackUrl ?: ""
            }
            url += "/payments"

            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            when (response.status) {
                HttpStatusCode.OK -> response.body<PaymentProcessorCreateResponse>().message
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun serviceHealth(type: PaymentType): PaymentProcessorHealthResponse? {
        var url: String = if (type == PaymentType.DEFAULT) {
            defaultUrl ?: ""
        } else {
            fallbackUrl ?: ""
        }
        url += "/payments/service-health"

        return try {
            val response = httpClient.get(url)

            when (response.status) {
                HttpStatusCode.OK -> response.body()
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}