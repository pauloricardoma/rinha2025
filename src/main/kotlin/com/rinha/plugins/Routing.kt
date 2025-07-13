package com.rinha.plugins

import com.rinha.routing.paymentsRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        paymentsRouting()
    }
}
