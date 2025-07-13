package com.rinha

import com.rinha.plugins.configureKoin
import com.rinha.plugins.configureRouting
import com.rinha.plugins.configureWorkers
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureWorkers()
    configureKoin()
}
