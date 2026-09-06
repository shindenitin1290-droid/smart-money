package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.CashProfileEntity
import com.example.data.local.entities.InvestmentEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.data.repository.ExpenseTrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class BankAccountWithBalance(
    val account: BankAccountEntity,
    val currentBalance: Double
)

data class ExpenseByCategoryItem(
    val categoryId: String,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

data class ExpenseTrackerUiState(
    val cashOpeningBalance: Double = 0.0,
    val cashCurrentBalance: Double = 0.0,
    val bankAccounts: List<BankAccountWithBalance> = emptyList(),
    val totalBankBalance: Double = 0.0,
    val investments: List<InvestmentEntity> = emptyList(),
    val totalInvestmentInvested: Double = 0.0,
    val totalInvestmentCurrent: Double = 0.0,
    val totalInvestmentReturns: Double = 0.0,
    val totalNetWorth: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val allTransactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val expenseByCategory: List<ExpenseByCategoryItem> = emptyList(),
    val selectedTab: Int = 0, // 0: Dashboard/Transactions, 1: Accounts/Cash&Bank, 2: Investments, 3: Analytics
    val filterType: String = "ALL", // ALL, CREDIT, DEBIT, TRANSFER
    val filterAccount: String = "ALL", // ALL, CASH, or BANK_<id>
    val filterCategory: String = "ALL",
    val searchQuery: String = "",
    val currencySymbol: String = "₹"
)

class ExpenseTrackerViewModel(
    private val repository: ExpenseTrackerRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    private val _filterType = MutableStateFlow("ALL")
    private val _filterAccount = MutableStateFlow("ALL")
    private val _filterCategory = MutableStateFlow("ALL")
    private val _searchQuery = MutableStateFlow("")
    private val _currencySymbol = MutableStateFlow("₹")

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    val uiState: StateFlow<ExpenseTrackerUiState> = combine(
        combine(
            repository.cashProfile,
            repository.bankAccounts,
            repository.investments,
            repository.allTransactions
        ) { cash, banks, investments, transactions ->
            computeFinancials(cash, banks, investments, transactions)
        },
        combine(
            _selectedTab,
            _filterType,
            _filterAccount,
            _filterCategory,
            _searchQuery
        ) { tab, type, account, category, query ->
            FilterBundle(tab, type, account, category, query)
        },
        _currencySymbol
    ) { financials, filters, currency ->
        val filtered = filterTransactions(
            transactions = financials.allTransactions,
            typeFilter = filters.type,
            accountFilter = filters.account,
            categoryFilter = filters.category,
            query = filters.query
        )

        ExpenseTrackerUiState(
            cashOpeningBalance = financials.cashOpening,
            cashCurrentBalance = financials.cashBalance,
            bankAccounts = financials.bankAccounts,
            totalBankBalance = financials.totalBankBalance,
            investments = financials.investments,
            totalInvestmentInvested = financials.totalInvestmentInvested,
            totalInvestmentCurrent = financials.totalInvestmentCurrent,
            totalInvestmentReturns = financials.totalInvestmentCurrent - financials.totalInvestmentInvested,
            totalNetWorth = financials.cashBalance + financials.totalBankBalance + financials.totalInvestmentCurrent,
            totalIncome = financials.totalIncome,
            totalExpense = financials.totalExpense,
            netSavings = financials.totalIncome - financials.totalExpense,
            allTransactions = financials.allTransactions,
            filteredTransactions = filtered,
            expenseByCategory = financials.expenseByCategory,
            selectedTab = filters.tab,
            filterType = filters.type,
            filterAccount = filters.account,
            filterCategory = filters.category,
            searchQuery = filters.query,
            currencySymbol = currency
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseTrackerUiState()
    )

    private fun filterTransactions(
        transactions: List<TransactionEntity>,
        typeFilter: String,
        accountFilter: String,
        categoryFilter: String,
        query: String
    ): List<TransactionEntity> {
        return transactions.filter { tx ->
            val matchesType = when (typeFilter) {
                "ALL" -> true
                else -> tx.type.equals(typeFilter, ignoreCase = true)
            }
            val matchesAccount = when (accountFilter) {
                "ALL" -> true
                "CASH" -> tx.accountType.equals("CASH", ignoreCase = true) || tx.toAccountType.equals("CASH", ignoreCase = true)
                else -> {
                    if (accountFilter.startsWith("BANK_")) {
                        val bankId = accountFilter.removePrefix("BANK_").toLongOrNull()
                        tx.bankAccountId == bankId || tx.toBankAccountId == bankId
                    } else true
                }
            }
            val matchesCategory = when (categoryFilter) {
                "ALL" -> true
                else -> tx.category.equals(categoryFilter, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) true else {
                tx.note.contains(query, ignoreCase = true) ||
                tx.category.contains(query, ignoreCase = true) ||
                tx.amount.toString().contains(query)
            }

            matchesType && matchesAccount && matchesCategory && matchesQuery
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun setFilterAccount(account: String) {
        _filterAccount.value = account
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCurrencySymbol(symbol: String) {
        _currencySymbol.value = symbol
    }

    fun updateCashOpeningBalance(amount: Double) {
        viewModelScope.launch {
            repository.setCashOpeningBalance(amount)
        }
    }

    fun addBankAccount(name: String, number: String, opening: Double, color: Long) {
        viewModelScope.launch {
            repository.addBankAccount(name, number, opening, color)
        }
    }

    fun updateBankAccount(account: BankAccountEntity) {
        viewModelScope.launch {
            repository.updateBankAccount(account)
        }
    }

    fun deleteBankAccount(account: BankAccountEntity) {
        viewModelScope.launch {
            repository.deleteBankAccount(account)
        }
    }

    fun addInvestment(
        name: String,
        type: String,
        openingBalance: Double,
        currentBalance: Double,
        institution: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addInvestment(name, type, openingBalance, currentBalance, institution, notes)
        }
    }

    fun updateInvestment(investment: InvestmentEntity) {
        viewModelScope.launch {
            repository.updateInvestment(investment)
        }
    }

    fun deleteInvestment(investment: InvestmentEntity) {
        viewModelScope.launch {
            repository.deleteInvestment(investment)
        }
    }

    fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        accountType: String,
        bankAccountId: Long?,
        toAccountType: String?,
        toBankAccountId: Long?,
        toInvestmentId: Long?,
        date: Long,
        note: String
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                type = type,
                amount = amount,
                category = category,
                accountType = accountType,
                bankAccountId = bankAccountId,
                toAccountType = toAccountType,
                toBankAccountId = toBankAccountId,
                toInvestmentId = toInvestmentId,
                date = date,
                note = note
            )
            repository.addTransaction(tx)

            // If it's a transfer to an investment, we can also update investment current value
            if (type == "TRANSFER" && toInvestmentId != null) {
                // Find investment and increment value
                val currentInv = uiState.value.investments.find { it.id == toInvestmentId }
                if (currentInv != null) {
                    repository.updateInvestment(
                        currentInv.copy(
                            currentBalance = currentInv.currentBalance + amount,
                            openingBalance = currentInv.openingBalance + amount
                        )
                    )
                }
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun deleteTransactionById(id: Long) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    companion object {
        fun formatAmount(amount: Double, currency: String = "₹"): String {
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            formatter.minimumFractionDigits = 2
            formatter.maximumFractionDigits = 2
            return "$currency${formatter.format(amount)}"
        }
    }
}

private data class FilterBundle(
    val tab: Int,
    val type: String,
    val account: String,
    val category: String,
    val query: String
)

private data class FinancialsComputation(
    val cashOpening: Double,
    val cashBalance: Double,
    val bankAccounts: List<BankAccountWithBalance>,
    val totalBankBalance: Double,
    val investments: List<InvestmentEntity>,
    val totalInvestmentInvested: Double,
    val totalInvestmentCurrent: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val allTransactions: List<TransactionEntity>,
    val expenseByCategory: List<ExpenseByCategoryItem>
)

private fun computeFinancials(
    cashProfile: CashProfileEntity?,
    bankAccounts: List<BankAccountEntity>,
    investments: List<InvestmentEntity>,
    transactions: List<TransactionEntity>
): FinancialsComputation {
    val cashOpening = cashProfile?.openingBalance ?: 0.0
    var cashBalance = cashOpening

    val bankBalances = mutableMapOf<Long, Double>()
    bankAccounts.forEach { bank ->
        bankBalances[bank.id] = bank.openingBalance
    }

    var totalIncome = 0.0
    var totalExpense = 0.0
    val expenseMap = mutableMapOf<String, Pair<Double, Int>>()

    for (tx in transactions) {
        when (tx.type) {
            "CREDIT" -> {
                totalIncome += tx.amount
                if (tx.accountType.equals("CASH", ignoreCase = true)) {
                    cashBalance += tx.amount
                } else if (tx.accountType.equals("BANK", ignoreCase = true) && tx.bankAccountId != null) {
                    val current = bankBalances[tx.bankAccountId] ?: 0.0
                    bankBalances[tx.bankAccountId] = current + tx.amount
                }
            }
            "DEBIT" -> {
                totalExpense += tx.amount
                if (tx.accountType.equals("CASH", ignoreCase = true)) {
                    cashBalance -= tx.amount
                } else if (tx.accountType.equals("BANK", ignoreCase = true) && tx.bankAccountId != null) {
                    val current = bankBalances[tx.bankAccountId] ?: 0.0
                    bankBalances[tx.bankAccountId] = current - tx.amount
                }

                // Accumulate category stats
                val existing = expenseMap[tx.category] ?: Pair(0.0, 0)
                expenseMap[tx.category] = Pair(existing.first + tx.amount, existing.second + 1)
            }
            "TRANSFER" -> {
                // Source deduction
                if (tx.accountType.equals("CASH", ignoreCase = true)) {
                    cashBalance -= tx.amount
                } else if (tx.accountType.equals("BANK", ignoreCase = true) && tx.bankAccountId != null) {
                    val current = bankBalances[tx.bankAccountId] ?: 0.0
                    bankBalances[tx.bankAccountId] = current - tx.amount
                }

                // Destination addition
                if (tx.toAccountType.equals("CASH", ignoreCase = true)) {
                    cashBalance += tx.amount
                } else if (tx.toAccountType.equals("BANK", ignoreCase = true) && tx.toBankAccountId != null) {
                    val current = bankBalances[tx.toBankAccountId] ?: 0.0
                    bankBalances[tx.toBankAccountId] = current + tx.amount
                }
            }
        }
    }

    val bankAccountsWithBalance = bankAccounts.map { bank ->
        BankAccountWithBalance(
            account = bank,
            currentBalance = bankBalances[bank.id] ?: bank.openingBalance
        )
    }
    val totalBankBalance = bankAccountsWithBalance.sumOf { it.currentBalance }

    val totalInvested = investments.sumOf { it.openingBalance }
    val totalCurrent = investments.sumOf { it.currentBalance }

    // Categories breakdown
    val categoryList = expenseMap.map { (catId, pair) ->
        val catItem = CategoryRegistry.getCategory(catId, isCredit = false)
        val percentage = if (totalExpense > 0) ((pair.first / totalExpense) * 100).toFloat() else 0f
        ExpenseByCategoryItem(
            categoryId = catId,
            categoryName = catItem.displayName,
            totalAmount = pair.first,
            percentage = percentage,
            transactionCount = pair.second
        )
    }.sortedByDescending { it.totalAmount }

    return FinancialsComputation(
        cashOpening = cashOpening,
        cashBalance = cashBalance,
        bankAccounts = bankAccountsWithBalance,
        totalBankBalance = totalBankBalance,
        investments = investments,
        totalInvestmentInvested = totalInvested,
        totalInvestmentCurrent = totalCurrent,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        allTransactions = transactions,
        expenseByCategory = categoryList
    )
}

class ExpenseTrackerViewModelFactory(
    private val repository: ExpenseTrackerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseTrackerViewModel::class.java)) {
            return ExpenseTrackerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
