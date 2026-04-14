package com.tonetower.app

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

// We define this right here to make sure IdGenerator ALWAYS sees it
enum class TransactionType {
    SETUP, STUDIO
}

object IdGenerator {

    fun generateReferenceId(type: TransactionType): String {
        val currentYear = LocalDate.now().year

        // We use .toLong() to avoid type mismatch and ensure the count is captured
        val count = transaction {
            when (type) {
                TransactionType.SETUP -> SetupTransactions.selectAll().count()
                TransactionType.STUDIO -> StudioBookings.selectAll().count()
            }
        }

        val prefix = when (type) {
            TransactionType.SETUP -> "STP"
            TransactionType.STUDIO -> "STD"
        }

        val nextNumber = count + 1

        // Simple string formatting to ensure at least 3 digits
        val sequence = if (nextNumber < 1000) {
            nextNumber.toString().padStart(3, '0')
        } else {
            nextNumber.toString()
        }

        return "TT-$prefix-$currentYear-$sequence"
    }
}