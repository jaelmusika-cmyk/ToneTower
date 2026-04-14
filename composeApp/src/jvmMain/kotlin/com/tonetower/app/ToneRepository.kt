package com.tonetower.app

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

// Explicitly importing the table objects from your Database.kt
import com.tonetower.app.SetupTransactions
import com.tonetower.app.AdminSettings
import com.tonetower.app.SetupsTable

object ToneRepository {

    // --- NEW SETUP INTAKE (PHASE 3) ---

    fun saveSetupJob(job: SetupJob) {
        transaction {
            SetupsTable.insert {
                it[clientName] = job.clientName
                it[clientPhone] = job.clientPhone
                it[instrumentModel] = job.instrumentModel
                it[serialNumber] = job.serialNumber
                it[dateAdded] = job.dateAdded
                it[totalFee] = job.totalFee
                it[servicesDone] = job.servicesDone
            }
        }
    }

    fun getAllSetupJobs(): List<SetupJob> {
        return transaction {
            SetupsTable.selectAll()
                .orderBy(SetupsTable.dateAdded to SortOrder.DESC)
                .map {
                    SetupJob(
                        id = it[SetupsTable.id],
                        clientName = it[SetupsTable.clientName],
                        clientPhone = it[SetupsTable.clientPhone],
                        instrumentModel = it[SetupsTable.instrumentModel],
                        serialNumber = it[SetupsTable.serialNumber],
                        dateAdded = it[SetupsTable.dateAdded],
                        totalFee = it[SetupsTable.totalFee],
                        servicesDone = it[SetupsTable.servicesDone]
                    )
                }
        }
    }

    // --- LEGACY SETUP TRANSACTIONS ---

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
            val exists = AdminSettings.select { AdminSettings.key eq settingKey }.any()

            if (exists) {
                AdminSettings.update({ AdminSettings.key eq settingKey }) {
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