package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.InvestmentEntity
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InvestmentsScreen
import com.example.ui.screens.dialogs.AddEditBankDialog
import com.example.ui.screens.dialogs.AddEditInvestmentDialog
import com.example.ui.screens.dialogs.AddEditTransactionDialog
import com.example.ui.screens.dialogs.EditCashBalanceDialog
import com.example.ui.viewmodel.ExpenseTrackerViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SleekOnPrimaryContainer
import com.example.ui.theme.SleekOnSecondaryContainer
import com.example.ui.theme.SleekOutlineVariant
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp(
    viewModel: ExpenseTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddTxDialog by remember { mutableStateOf(false) }
    var showEditCashDialog by remember { mutableStateOf(false) }
    var showAddBankDialog by remember { mutableStateOf(false) }
    var editingBank by remember { mutableStateOf<BankAccountEntity?>(null) }
    var showAddInvestmentDialog by remember { mutableStateOf(false) }
    var editingInvestment by remember { mutableStateOf<InvestmentEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SleekSurface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SleekSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Expense Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SleekTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SleekSurface
                ),
                actions = {
                    // Currency Switcher Button with Sleek Interface pill
                    IconButton(
                        onClick = {
                            val newCurrency = if (uiState.currencySymbol == "₹") "$" else "₹"
                            viewModel.setCurrencySymbol(newCurrency)
                        },
                        modifier = Modifier.testTag("currency_switch_button")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SleekSecondaryContainer
                        ) {
                            Text(
                                text = uiState.currencySymbol,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = SleekPrimary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SleekSurfaceVariant,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SleekOnSecondaryContainer,
                    selectedTextColor = SleekOnSecondaryContainer,
                    indicatorColor = SleekSecondaryContainer,
                    unselectedIconColor = SleekTextSecondary,
                    unselectedTextColor = SleekTextSecondary
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = {
                        Icon(
                            if (uiState.selectedTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Overview"
                        )
                    },
                    label = { Text("Overview", fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_overview")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = {
                        Icon(
                            if (uiState.selectedTab == 1) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance,
                            contentDescription = "Accounts"
                        )
                    },
                    label = { Text("Accounts", fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_accounts")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = {
                        Icon(
                            if (uiState.selectedTab == 2) Icons.Filled.ShowChart else Icons.Outlined.ShowChart,
                            contentDescription = "Investments"
                        )
                    },
                    label = { Text("Investments", fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_investments")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = {
                        Icon(
                            if (uiState.selectedTab == 3) Icons.Filled.PieChart else Icons.Outlined.PieChart,
                            contentDescription = "Analytics"
                        )
                    },
                    label = { Text("Analytics", fontWeight = if (uiState.selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_analytics")
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTxDialog = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = SleekPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Transaction") },
                text = { Text("Add Transaction", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("fab_add_transaction")
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                0 -> DashboardScreen(
                    uiState = uiState,
                    onEditCashClick = { showEditCashDialog = true },
                    onAddTransactionClick = { showAddTxDialog = true },
                    onFilterTypeChange = { viewModel.setFilterType(it) },
                    onFilterAccountChange = { viewModel.setFilterAccount(it) },
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onDeleteTransaction = { viewModel.deleteTransaction(it) }
                )
                1 -> AccountsScreen(
                    uiState = uiState,
                    onEditCashClick = { showEditCashDialog = true },
                    onAddBankClick = {
                        editingBank = null
                        showAddBankDialog = true
                    },
                    onEditBankClick = { bank ->
                        editingBank = bank
                        showAddBankDialog = true
                    },
                    onDeleteBankClick = { viewModel.deleteBankAccount(it) }
                )
                2 -> InvestmentsScreen(
                    uiState = uiState,
                    onAddInvestmentClick = {
                        editingInvestment = null
                        showAddInvestmentDialog = true
                    },
                    onEditInvestmentClick = { inv ->
                        editingInvestment = inv
                        showAddInvestmentDialog = true
                    },
                    onDeleteInvestmentClick = { viewModel.deleteInvestment(it) }
                )
                3 -> AnalyticsScreen(
                    uiState = uiState
                )
            }
        }
    }

    // Dialogs
    if (showAddTxDialog) {
        AddEditTransactionDialog(
            bankAccounts = uiState.bankAccounts,
            investments = uiState.investments,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { showAddTxDialog = false },
            onSaveTransaction = { type, amount, category, accountType, bankId, toAccType, toBankId, toInvId, date, note ->
                viewModel.addTransaction(
                    type = type,
                    amount = amount,
                    category = category,
                    accountType = accountType,
                    bankAccountId = bankId,
                    toAccountType = toAccType,
                    toBankAccountId = toBankId,
                    toInvestmentId = toInvId,
                    date = date,
                    note = note
                )
            }
        )
    }

    if (showEditCashDialog) {
        EditCashBalanceDialog(
            currentOpeningBalance = uiState.cashOpeningBalance,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { showEditCashDialog = false },
            onSave = { amount -> viewModel.updateCashOpeningBalance(amount) }
        )
    }

    if (showAddBankDialog) {
        AddEditBankDialog(
            existingBank = editingBank,
            currencySymbol = uiState.currencySymbol,
            onDismiss = {
                showAddBankDialog = false
                editingBank = null
            },
            onSave = { name, number, opening, color ->
                if (editingBank != null) {
                    viewModel.updateBankAccount(
                        editingBank!!.copy(
                            bankName = name,
                            accountNumber = number,
                            openingBalance = opening,
                            colorHex = color
                        )
                    )
                } else {
                    viewModel.addBankAccount(name, number, opening, color)
                }
            },
            onDelete = if (editingBank != null) {
                { viewModel.deleteBankAccount(editingBank!!) }
            } else null
        )
    }

    if (showAddInvestmentDialog) {
        AddEditInvestmentDialog(
            existingInvestment = editingInvestment,
            currencySymbol = uiState.currencySymbol,
            onDismiss = {
                showAddInvestmentDialog = false
                editingInvestment = null
            },
            onSave = { name, type, opening, current, institution, notes ->
                if (editingInvestment != null) {
                    viewModel.updateInvestment(
                        editingInvestment!!.copy(
                            name = name,
                            type = type,
                            openingBalance = opening,
                            currentBalance = current,
                            institution = institution,
                            notes = notes
                        )
                    )
                } else {
                    viewModel.addInvestment(name, type, opening, current, institution, notes)
                }
            },
            onDelete = if (editingInvestment != null) {
                { viewModel.deleteInvestment(editingInvestment!!) }
            } else null
        )
    }
}
