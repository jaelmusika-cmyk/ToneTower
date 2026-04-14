package com.tonetower.app

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

// Explicitly importing the table objects from your Database.kt
import com.tonetower.app.SetupTransactions
import com.tonetower.app.AdminSettings

object ToneRepository {

    // --- SETUP TRANSACTIONS ---

    fun saveSetup(setup: SetupTransaction) {
        transaction {
            SetupTransactions.insert {
                it[referenceId] = setup.referenceId
                it[clientName] = setup.clientName
                it[instrument] = setup.instrument
                it[model] = setup.model
                it[totalPrice] = setup.totalPrice
                it[status] = setup.status
                it[servicesJson] = Json.encodeToString(setup.services)
            }
        }
    }

    fun getAllSetups(): List<SetupTransaction> {
        return transaction {
            SetupTransactions.selectAll().map {
                SetupTransaction(
                    referenceId = it[SetupTransactions.referenceId],
                    clientName = it[SetupTransactions.clientName],
                    instrument = it[SetupTransactions.instrument],
                    model = it[SetupTransactions.model],
                    totalPrice = it[SetupTransactions.totalPrice],
                    status = it[SetupTransactions.status],
                    services = Json.decodeFromString(it[SetupTransactions.servicesJson])
                )
            }
        }
    }

    // --- ADMIN SETTINGS ---

    fun saveSetting(settingKey: String, settingValue: Double) {
        transaction {
            // Check if the setting already exists using the explicit table reference
            val exists = AdminSettings.select { AdminSettings.key eq settingKey }.any()

            if (exists) {
                AdminSettings.update({ AdminSettings.key eq settingKey }) {
                    // Explicitly using AdminSettings.value to fix the "Unresolved reference"
                    it[AdminSettings.value] = settingValue
                }
            } else {
                AdminSettings.insert {
                    it[AdminSettings.key] = settingKey
                    it[AdminSettings.value] = settingValue
                }
            }
        }
    }

    fun getSetting(settingKey: String, default: Double): Double {
        return transaction {
            AdminSettings.select { AdminSettings.key eq settingKey }
                .map { it[AdminSettings.value] }
                .singleOrNull() ?: default
        }
    }
}