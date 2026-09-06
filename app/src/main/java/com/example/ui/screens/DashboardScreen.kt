package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TransactionEntity
import com.example.ui.components.IncomeExpenseSummaryRow
import com.example.ui.components.NetWorthHeroCard
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.CreditGreen
import com.example.ui.theme.DebitRed
import com.example.ui.theme.SleekCreditGreen
import com.example.ui.theme.SleekCreditGreenContainer
import com.example.ui.theme.SleekDebitRed
import com.example.ui.theme.SleekDebitRedContainer
import com.example.ui.theme.SleekOnPrimaryContainer
import com.example.ui.theme.SleekOnSecondaryContainer
import com.example.ui.theme.SleekOutline
import com.example.ui.theme.SleekOutlineVariant
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSubCard
import com.example.ui.theme.SleekSurfaceCard
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekTransferBlue
import com.example.ui.theme.SleekTransferBlueContainer
import com.example.ui.theme.TransferBlue
import com.example.ui.viewmodel.ExpenseTrackerUiState
import com.example.ui.viewmodel.ExpenseTrackerViewModel

@Composable
fun DashboardScreen(
    uiState: ExpenseTrackerUiState,
    onEditCashClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onFilterTypeChange: (String) -> Unit,
    onFilterAccountChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp, top = 12.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card (Sleek lavender net worth)
        item {
            NetWorthHeroCard(
                netWorth = uiState.totalNetWorth,
                cashBalance = uiState.cashCurrentBalance,
                bankBalance = uiState.totalBankBalance,
                investmentBalance = uiState.totalInvestmentCurrent,
                currencySymbol = uiState.currencySymbol,
                onEditCashClick = onEditCashClick
            )
        }

        // Quick Bank Accounts Carousel (Sleek rounded-2xl pills)
        if (uiState.bankAccounts.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ACCOUNTS & CARDS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = SleekTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.bankAccounts) { bankWithBal ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = SleekSurfaceCard,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = SleekOutlineVariant,
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .clickable { onFilterAccountChange("BANK_${bankWithBal.account.id}") }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(SleekSecondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bankWithBal.account.bankName.take(1).uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = SleekPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${bankWithBal.account.bankName} • ${bankWithBal.account.accountNumber.takeLast(4)}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = SleekTextPrimary
                                        )
                                        Text(
                                            text = ExpenseTrackerViewModel.formatAmount(bankWithBal.currentBalance, uiState.currencySymbol),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            ),
                                            color = SleekTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Income & Expense Row
        item {
            IncomeExpenseSummaryRow(
                income = uiState.totalIncome,
                expense = uiState.totalExpense,
                currencySymbol = uiState.currencySymbol
            )
        }

        // Filter and Search Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekTextPrimary
                    )

                    IconButton(
                        onClick = {
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) onSearchQueryChange("")
                        },
                        modifier = Modifier.testTag("toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SleekTextSecondary
                        )
                    }
                }

                if (isSearchActive) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search transactions, notes, categories...") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SleekTextSecondary) },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = SleekTextSecondary)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transaction_search_input")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Type Filter Chips (Sleek styling)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterTypes = listOf(
                        "ALL" to "All",
                        "CREDIT" to "Credit",
                        "DEBIT" to "Debit",
                        "TRANSFER" to "Transfer"
                    )

                    items(filterTypes) { (key, label) ->
                        val isSelected = uiState.filterType == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterTypeChange(key) },
                            shape = RoundedCornerShape(12.dp),
                            label = {
                                Text(
                                    label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (key) {
                                    "CREDIT" -> SleekCreditGreenContainer
                                    "DEBIT" -> SleekDebitRedContainer
                                    "TRANSFER" -> SleekTransferBlueContainer
                                    else -> SleekSecondaryContainer
                                },
                                selectedLabelColor = when (key) {
                                    "CREDIT" -> SleekCreditGreen
                                    "DEBIT" -> SleekDebitRed
                                    "TRANSFER" -> SleekTransferBlue
                                    else -> SleekOnSecondaryContainer
                                }
                            ),
                            modifier = Modifier.testTag("filter_chip_$key")
                        )
                    }
                }

                // Account Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.filterAccount == "ALL",
                            onClick = { onFilterAccountChange("ALL") },
                            shape = RoundedCornerShape(12.dp),
                            label = { Text("All Accounts", fontSize = 11.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.filterAccount == "CASH",
                            onClick = { onFilterAccountChange("CASH") },
                            shape = RoundedCornerShape(12.dp),
                            label = { Text("Cash Only", fontSize = 11.sp) }
                        )
                    }
                    items(uiState.bankAccounts) { bank ->
                        val bankKey = "BANK_${bank.account.id}"
                        FilterChip(
                            selected = uiState.filterAccount == bankKey,
                            onClick = { onFilterAccountChange(bankKey) },
                            shape = RoundedCornerShape(12.dp),
                            label = { Text(bank.account.bankName, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Transactions List or Empty State
        if (uiState.filteredTransactions.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .testTag("empty_transactions_state"),
                    shape = RoundedCornerShape(24.dp),
                    color = SleekSurfaceCard
                ) {
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = SleekOutlineVariant,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SleekSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Transactions Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SleekTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap Add Transaction to record credit income, debit expense, or transfers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.filteredTransactions, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    bankAccounts = uiState.bankAccounts,
                    currencySymbol = uiState.currencySymbol,
                    onDeleteClick = onDeleteTransaction
                )
            }
        }
    }
}
