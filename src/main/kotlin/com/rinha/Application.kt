package com.rinha

import com.rinha.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureRouting()
    configureWorkers()
}
