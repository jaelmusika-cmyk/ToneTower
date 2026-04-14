package com.tonetower.app

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object ToneRepository {

    fun saveSetup(setup: SetupTransaction) {
        transaction {
            SetupTransactions.insert {
                it[referenceId] = setup.referenceId
                it[clientName] = setup.clientName
                it[instrument] = setup.instrument
                it[model] = setup.model
                it[totalPrice] = setup.totalPrice
                it[status] = setup.status
                // We turn the List of services into a JSON string to store it
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
}