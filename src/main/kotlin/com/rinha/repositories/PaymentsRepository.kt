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
import java.sql.Timestamp
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

class PaymentsRepository: KoinComponent {
    private val dataSource by inject<HikariDataSource>()
    private val paymentService by inject<PaymentProcessorService>()

    @OptIn(ExperimentalTime::class)
    suspend fun create(payment: Payment): Boolean {
        return try {
            dataSource.connection.use { conn ->
                conn.autoCommit = false

                try {
                    val query = "INSERT INTO payments (correlationId, amount, type, createdAt) VALUES(?, ?, ?, ?);"
                    val requestedAt = Clock.System.now()

                    conn.prepareStatement(query).use { stmt ->
                        stmt.setString(1, payment.correlationId)
                        stmt.setDouble(2, payment.amount)
                        stmt.setInt(3, payment.type.value)
                        stmt.setTimestamp(4, Timestamp.from(requestedAt.toJavaInstant()))

                        val rowsAffected = stmt.executeUpdate()

                        if (rowsAffected == 0) {
                            throw SQLException()
                        }
                    }

                    val paymentProcessorRequest = PaymentProcessorCreateRequest(
                        correlationId = payment.correlationId,
                        amount = payment.amount,
                        type = payment.type,
                        requestedAt = requestedAt
                    )
                    val paymentProcessorResponse = paymentService.create(paymentProcessorRequest)

                    if (paymentProcessorResponse == null) {
                        throw Exception()
                    }

                    conn.commit()
                    true
                } catch (e: Exception) {
                    conn.rollback()
                    print("ERROR: ${e.message}")
                    false
                }
            }
        } catch (e: Exception) {
            print("ERROR: ${e.message}")
            false
        }
    }

    fun summary(fromDate: String?, toDate: String?): PaymentSummary? {
        return try {
            val response = PaymentSummary(
                default = TotalPayment(0, 0.0),
                fallback = TotalPayment(0, 0.0)
            )

            dataSource.connection.use { conn ->
                var query = "SELECT type, COUNT(*) as totalRequests, SUM(amount) as totalAmount FROM payments"

                val params = mutableListOf<String>()
                if (fromDate != null && toDate != null) {
                    params.add(fromDate)
                    params.add(toDate)
                    query += " WHERE createdAt BETWEEN ?::timestamptz AND ?::timestamptz"
                }
                if (fromDate != null && params.isEmpty()) {
                    params.add(fromDate)
                    query += " WHERE createdAt >= ?::timestamptz"
                }
                if (toDate != null && params.isEmpty()) {
                    params.add(toDate)
                    query += " WHERE createdAt <= ?::timestamptz"
                }
                query += "GROUP BY type;"

                conn.prepareStatement(query).use { stmt ->
                    for (i in params.indices) {
                        stmt.setString(i+1, params[i])
                    }
                    val data = stmt.executeQuery()

                    while (data.next()) {
                        val rowType = data.getInt(1)
                        val rowTotalRequests = data.getInt(2)
                        val rowTotalAmount = data.getDouble(3)

                        if (rowType == PaymentType.DEFAULT.value) {
                            response.default.totalRequests = rowTotalRequests
                            response.default.totalAmount = rowTotalAmount
                        } else if (rowType == PaymentType.FALLBACK.value) {
                            response.fallback.totalRequests = rowTotalRequests
                            response.fallback.totalAmount = rowTotalAmount
                        }
                    }
                }
            }

            return response
        } catch (e: Exception) {
            null
        }
    }
}