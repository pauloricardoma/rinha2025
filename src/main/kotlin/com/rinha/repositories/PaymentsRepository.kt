package com.rinha.repositories

import com.rinha.models.Payment
import com.rinha.models.PaymentProcessorCreateRequest
import com.rinha.models.PaymentSummary
import com.rinha.models.PaymentType
import com.rinha.models.TotalPayment
import com.rinha.services.PaymentProcessorService
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.SQLException

class PaymentsRepository: KoinComponent {
    private val dataSource by inject<HikariDataSource>()
    private val paymentService by inject<PaymentProcessorService>()

    suspend fun create(payment: Payment): Boolean {
        return try {
            dataSource.connection.use { conn ->
                conn.autoCommit = false

                try {
                    val query = "INSERT INTO payments (correlationId, amount, type) VALUES(?, ?, ?);"

                    conn.prepareStatement(query).use { stmt ->
                        stmt.setString(1, payment.correlationId)
                        stmt.setDouble(2, payment.amount)
                        stmt.setInt(3, payment.type.value)

                        val rowsAffected = stmt.executeUpdate()

                        if (rowsAffected == 0) {
                            throw SQLException()
                        }
                    }

                    val paymentProcessorRequest = PaymentProcessorCreateRequest(
                        correlationId = payment.correlationId,
                        amount = payment.amount,
                        type = payment.type
                    )
                    val paymentProcessorResponse = paymentService.create(paymentProcessorRequest)

                    if (paymentProcessorResponse == null) {
                        throw Exception()
                    }

                    conn.commit()
                    true
                } catch (e: Exception) {
                    conn.rollback()
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    fun summary(fromDate: String?, toDate: String?): PaymentSummary {
        val response = PaymentSummary(
            default = TotalPayment(
                totalRequests = 0,
                totalAmount = 0.0
            ),
            fallback = TotalPayment(
                totalRequests = 0,
                totalAmount = 0.0
            )
        )
        val params = arrayOf<String>()

        dataSource.connection.use { conn ->
            var query = "SELECT amount, type FROM payments"

            if (fromDate != null && toDate != null) {
                params[0] = fromDate
                params[1] = toDate
                query += " WHERE createdAt BETWEEN ?::timestamptz AND ?::timestamptz"
            }
            if (fromDate != null && params.isEmpty()) {
                params[0] = fromDate
                query += " WHERE createdAt >= ?::timestamptz"
            }
            if (toDate != null && params.isEmpty()) {
                params[0] = toDate
                query += " WHERE createdAt <= ?::timestamptz"
            }
            query += ";"

            conn.prepareStatement(query).use { stmt ->
                for (i in params.indices) {
                    stmt.setString(i+1, params[i])
                }
                val data = stmt.executeQuery()

                while (data.next()) {
                    val rowAmount = data.getDouble(1)
                    val rowType = data.getInt(2)

                    if (rowType == PaymentType.DEFAULT.value) {
                        response.default.totalRequests++
                        response.default.totalAmount += rowAmount
                    }
                    if (rowType == PaymentType.FALLBACK.value) {
                        response.fallback.totalRequests++
                        response.fallback.totalAmount += rowAmount
                    }
                }
            }
        }

        return response
    }
}