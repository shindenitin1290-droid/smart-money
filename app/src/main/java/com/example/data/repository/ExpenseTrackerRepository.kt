package com.example.data.repository

import com.example.data.local.daos.BankAccountDao
import com.example.data.local.daos.CashDao
import com.example.data.local.daos.InvestmentDao
import com.example.data.local.daos.TransactionDao
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.CashProfileEntity
import com.example.data.local.entities.InvestmentEntity
import com.example.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

class ExpenseTrackerRepository(
    private val cashDao: CashDao,
    private val bankAccountDao: BankAccountDao,
    private val investmentDao: InvestmentDao,
    private val transactionDao: TransactionDao
) {
    val cashProfile: Flow<CashProfileEntity?> = cashDao.getCashProfile()
    val bankAccounts: Flow<List<BankAccountEntity>> = bankAccountDao.getAllBankAccounts()
    val investments: Flow<List<InvestmentEntity>> = investmentDao.getAllInvestments()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    suspend fun setCashOpeningBalance(amount: Double) {
        val current = cashDao.getCashProfileSync()
        val updated = current?.copy(openingBalance = amount, lastUpdated = System.currentTimeMillis())
            ?: CashProfileEntity(openingBalance = amount)
        cashDao.insertOrUpdateCashProfile(updated)
    }

    suspend fun addBankAccount(bankName: String, accountNumber: String, openingBalance: Double, colorHex: Long): Long {
        return bankAccountDao.insertBankAccount(
            BankAccountEntity(
                bankName = bankName,
                accountNumber = accountNumber,
                openingBalance = openingBalance,
                colorHex = colorHex
            )
        )
    }

    suspend fun updateBankAccount(bankAccount: BankAccountEntity) {
        bankAccountDao.updateBankAccount(bankAccount)
    }

    suspend fun deleteBankAccount(bankAccount: BankAccountEntity) {
        bankAccountDao.deleteBankAccount(bankAccount)
    }

    suspend fun addInvestment(
        name: String,
        type: String,
        openingBalance: Double,
        currentBalance: Double,
        institution: String,
        notes: String
    ): Long {
        return investmentDao.insertInvestment(
            InvestmentEntity(
                name = name,
                type = type,
                openingBalance = openingBalance,
                currentBalance = if (currentBalance > 0) currentBalance else openingBalance,
                institution = institution,
                notes = notes
            )
        )
    }

    suspend fun updateInvestment(investment: InvestmentEntity) {
        investmentDao.updateInvestment(investment)
    }

    suspend fun deleteInvestment(investment: InvestmentEntity) {
        investmentDao.deleteInvestment(investment)
    }

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val existingCash = cashDao.getCashProfileSync()
        if (existingCash == null) {
            cashDao.insertOrUpdateCashProfile(
                CashProfileEntity(id = 1, openingBalance = 5000.0)
            )
        }
    }
}
