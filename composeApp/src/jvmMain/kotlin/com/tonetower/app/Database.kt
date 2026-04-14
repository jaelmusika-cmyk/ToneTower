package com.tonetower.app

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

// --- TABLES ---

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

object StudioBookings : Table("studio_bookings") {
    val referenceId = varchar("reference_id", 50)
    val clientName = varchar("client_name", 255)
    val dateBookedFor = date("date_booked_for")
    val totalPrice = double("total_price")
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(referenceId)
}

// NEW TABLE FOR PHASE 2 - DO NOT MISS THIS
object AdminSettings : Table("admin_settings") {
    val key = varchar("setting_key", 100)
    val value = double("setting_value")
    override val primaryKey = PrimaryKey(key)
}

// --- MANAGER ---

object DatabaseManager {
    fun init() {
        val dbFile = File(System.getProperty("user.home"), "tonetower_v1.db")
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC")

        transaction {
            // Added AdminSettings here so the table actually gets created
            SchemaUtils.create(SetupTransactions, StudioBookings, AdminSettings)
        }
    }
}