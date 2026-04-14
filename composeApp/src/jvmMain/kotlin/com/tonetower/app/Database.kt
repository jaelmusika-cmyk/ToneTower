package com.tonetower.app

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

// --- TABLES ---

// Used for historical/high-level transaction tracking
object SetupTransactions : Table("setup_transactions") {
    val referenceId = varchar("reference_id", 50)
    val clientName = varchar("client_name", 255)
    val instrument = varchar("instrument", 100)
    val model = varchar("model", 100)
    val servicesJson = text("services_json")
    val totalPrice = double("total_price")
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(referenceId)
}

// Updated for Phase B: Logistics & Financials
object SetupsTable : Table("setups") {
    val id = integer("id").autoIncrement()
    val referenceId = varchar("reference_id", 50).uniqueIndex()
    val clientName = varchar("client_name", 255)
    val clientPhone = varchar("client_phone", 50)
    val instrumentModel = varchar("instrument_model", 255)
    val serialNumber = varchar("serial_number", 255)
    val dateAdded = long("date_added")
    val totalFee = double("total_fee")
    val servicesDone = text("services_done")
    val status = varchar("status", 20).default("Pending")

    // --- NEW PHASE B COLUMNS ---
    val inboundMethod = varchar("inbound_method", 50).default("Walk-in")
    val logisticsInfo = text("logistics_info").default("")
    val paymentMode = varchar("payment_mode", 50).default("Cash")
    val amountTendered = double("amount_tendered").default(0.0)
    val changeDue = double("change_due").default(0.0)

    override val primaryKey = PrimaryKey(id)
}

object StudioBookings : Table("studio_bookings") {
    val referenceId = varchar("reference_id", 50)
    val clientName = varchar("client_name", 255)
    val dateBookedFor = date("date_booked_for")
    val totalPrice = double("total_price")
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(referenceId)
}

object AdminSettings : Table("admin_settings") {
    val key = varchar("setting_key", 100)
    val value = double("setting_value")
    override val primaryKey = PrimaryKey(key)
}

// --- MANAGER ---

object DatabaseManager {
    fun init() {
        // Storing the DB in the user home folder ensures it persists even if the app folder moves
        val dbFile = File(System.getProperty("user.home"), "tonetower_v1.db")
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC")

        transaction {
            // CRITICAL: All tables must be listed here to be created on startup
            SchemaUtils.create(
                SetupTransactions,
                StudioBookings,
                AdminSettings,
                SetupsTable
            )
        }
    }
}