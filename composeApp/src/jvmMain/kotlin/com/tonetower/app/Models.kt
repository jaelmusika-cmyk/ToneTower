package com.tonetower.app

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class ServiceItem(
    val description: String,
    val price: Double
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

// Add this to Models.kt
data class AdminSetting(
    val key: String,
    val value: Double
)

// Add to Models.kt
enum class AppScreen {
    DASHBOARD,
    SETUPS,
    STUDIO,
    ADMIN
}