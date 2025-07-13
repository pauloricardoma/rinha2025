package com.rinha.plugins

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun configureHttpClient(): HttpClient {
    val clientHttp = HttpClient(OkHttp) {
        engine {
            val connectionPool = ConnectionPool(
                maxIdleConnections = 10,
                keepAliveDuration = 15,
                timeUnit = TimeUnit.SECONDS
            )

            val dispatcher = Dispatcher().apply {
                maxRequests = 30
                maxRequestsPerHost = 8
            }

            addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Connection", "keep-alive")
                    .build()
                chain.proceed(request)
            }

            preconfigured = OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }
    }

    return clientHttp
}