package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_profile")
data class CashProfileEntity(
    @PrimaryKey val id: Int = 1,
    val openingBalance: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bankName: String,
    val accountNumber: String,
    val openingBalance: Double = 0.0,
    val colorHex: Long = 0xFF0F766E,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val type: String, // "STOCK", "MUTUAL_FUND", "FIXED_DEPOSIT", "OTHER"
    val openingBalance: Double = 0.0, // Invested / Principal Amount
    val currentBalance: Double = 0.0, // Current Value
    val institution: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String, // "CREDIT", "DEBIT", "TRANSFER"
    val amount: Double,
    val category: String,
    val accountType: String, // "CASH", "BANK"
    val bankAccountId: Long? = null,
    val toAccountType: String? = null, // "CASH", "BANK", "INVESTMENT"
    val toBankAccountId: Long? = null,
    val toInvestmentId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)
