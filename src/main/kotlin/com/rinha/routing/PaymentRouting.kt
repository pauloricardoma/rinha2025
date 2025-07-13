package com.rinha.routing

import com.rinha.models.PaymentRequest
import com.rinha.repositories.PaymentsRepository
import com.rinha.services.PaymentQueue
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.paymentsRouting() {
    val paymentsRepository by inject<PaymentsRepository>()

    route("/payments") {
        post {
            try {
                val body = call.runCatching { receive<PaymentRequest>() }.getOrElse {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                PaymentQueue.queue.add(body)

                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.UnprocessableEntity)
                return@post
            }
        }
    }

    route("/payments-summary") {
        get {
            try {
                val fromDate = call.runCatching { request.queryParameters["from"] }.getOrNull()
                val toDate = call.runCatching { request.queryParameters["to"] }.getOrNull()

                val summary = paymentsRepository.summary(fromDate, toDate)

                call.respond(HttpStatusCode.OK,summary)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.UnprocessableEntity)
                return@get
            }
        }
    }
}