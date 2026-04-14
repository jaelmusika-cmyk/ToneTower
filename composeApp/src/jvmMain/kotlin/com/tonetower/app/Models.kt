package com.tonetower.app

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class ServiceItem(
    val description: String,
    val price: Double
)

// NEW: This model maps directly to your new "setups" table
@Serializable
data class SetupJob(
    val id: Int = 0,
    val clientName: String,
    val clientPhone: String,
    val instrumentModel: String,
    val serialNumber: String,
    val dateAdded: Long, // Use System.currentTimeMillis()
    val totalFee: Double,
    val servicesDone: String // Comma-separated list (e.g., "Restring, Deep Clean")
)

data class SetupTransaction(
    val referenceId: String,
    val clientName: String,
    val instrument: String,
    val model: String,
    val services: List<ServiceItem>,
    val totalPrice: Double,
    val status: String = "Pending"
)

data class StudioBooking(
    val referenceId: String,
    val clientName: String,
    val dateBooked: LocalDate,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val totalPrice: Double
)

data class AdminSetting(
    val key: String,
    val value: Double
)

enum class AppScreen {
    DASHBOARD,
    SETUPS,
    STUDIO,
    ADMIN
}