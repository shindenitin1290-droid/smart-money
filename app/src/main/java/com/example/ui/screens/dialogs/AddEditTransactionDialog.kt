package com.example.ui.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.InvestmentEntity
import com.example.data.model.CategoryItem
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.CreditGreen
import com.example.ui.theme.DebitRed
import com.example.ui.theme.TransferBlue
import com.example.ui.viewmodel.BankAccountWithBalance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionDialog(
    bankAccounts: List<BankAccountWithBalance>,
    investments: List<InvestmentEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSaveTransaction: (
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
    ) -> Unit
) {
    var selectedTypeIndex by remember { mutableStateOf(1) } // 0: Credit, 1: Debit, 2: Transfer
    val types = listOf("CREDIT", "DEBIT", "TRANSFER")
    val currentType = types[selectedTypeIndex]

    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember {
        mutableStateOf(if (selectedTypeIndex == 0) "salary" else "grocery")
    }
    var accountType by remember { mutableStateOf("CASH") } // "CASH" or "BANK"
    var selectedBankAccountId by remember { mutableStateOf(bankAccounts.firstOrNull()?.account?.id) }

    // For Transfer
    var toAccountType by remember { mutableStateOf("BANK") } // "CASH", "BANK", "INVESTMENT"
    var toBankAccountId by remember { mutableStateOf(bankAccounts.firstOrNull()?.account?.id) }
    var toInvestmentId by remember { mutableStateOf(investments.firstOrNull()?.id) }

    var noteText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .testTag("add_transaction_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Type Tab Row (Credit / Debit / Transfer)
                TabRow(
                    selectedTabIndex = selectedTypeIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTypeIndex == 0,
                        onClick = {
                            selectedTypeIndex = 0
                            selectedCategory = "salary"
                        },
                        text = {
                            Text(
                                "Credit (Income)",
                                fontWeight = if (selectedTypeIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTypeIndex == 0) CreditGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_credit")
                    )
                    Tab(
                        selected = selectedTypeIndex == 1,
                        onClick = {
                            selectedTypeIndex = 1
                            selectedCategory = "grocery"
                        },
                        text = {
                            Text(
                                "Debit (Expense)",
                                fontWeight = if (selectedTypeIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTypeIndex == 1) DebitRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_debit")
                    )
                    Tab(
                        selected = selectedTypeIndex == 2,
                        onClick = { selectedTypeIndex = 2 },
                        text = {
                            Text(
                                "Transfer",
                                fontWeight = if (selectedTypeIndex == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTypeIndex == 2) TransferBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_transfer")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable form content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Amount Field
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amountText = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Amount ($currencySymbol)") },
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Text(
                                text = currencySymbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transaction_amount_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentType != "TRANSFER") {
                        // Category Selector
                        val categoryList = if (currentType == "CREDIT") {
                            CategoryRegistry.incomeCategories
                        } else {
                            CategoryRegistry.expenseCategories
                        }

                        Text(
                            text = if (currentType == "CREDIT") "Select Income Source" else "Select Expenditure Category",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryList.forEach { cat ->
                                val isSelected = selectedCategory.equals(cat.id, ignoreCase = true) ||
                                        selectedCategory.equals(cat.displayName, ignoreCase = true)

                                CategoryChipItem(
                                    category = cat,
                                    isSelected = isSelected,
                                    onClick = { selectedCategory = cat.id }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Account Selection (Cash or Bank)
                        Text(
                            text = if (currentType == "CREDIT") "Deposit Into" else "Paid From",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Cash Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (accountType == "CASH") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (accountType == "CASH") androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { accountType = "CASH" }
                                    .testTag("select_account_cash")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Payments,
                                        contentDescription = "Cash",
                                        tint = if (accountType == "CASH") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Cash Wallet",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (accountType == "CASH") FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }

                            // Bank Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (accountType == "BANK") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (accountType == "BANK") androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        accountType = "BANK"
                                        if (selectedBankAccountId == null && bankAccounts.isNotEmpty()) {
                                            selectedBankAccountId = bankAccounts.first().account.id
                                        }
                                    }
                                    .testTag("select_account_bank")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        contentDescription = "Bank",
                                        tint = if (accountType == "BANK") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Bank Account",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (accountType == "BANK") FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }

                        // If Bank selected, show Bank Picker
                        if (accountType == "BANK") {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (bankAccounts.isEmpty()) {
                                Text(
                                    text = "No bank accounts added yet. Please add a bank account in the Accounts tab.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                BankDropdownPicker(
                                    label = "Select Bank Account",
                                    bankAccounts = bankAccounts,
                                    selectedBankId = selectedBankAccountId,
                                    onSelectBank = { selectedBankAccountId = it }
                                )
                            }
                        }
                    } else {
                        // TRANSFER MODE
                        Text(
                            text = "Transfer From",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (accountType == "CASH") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { accountType = "CASH" }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cash", style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (accountType == "BANK") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { accountType = "BANK" }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bank", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        if (accountType == "BANK" && bankAccounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            BankDropdownPicker(
                                label = "From Bank",
                                bankAccounts = bankAccounts,
                                selectedBankId = selectedBankAccountId,
                                onSelectBank = { selectedBankAccountId = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Transfer To",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (toAccountType == "CASH") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { toAccountType = "CASH" }
                            ) {
                                Text(
                                    "Cash",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (toAccountType == "CASH") FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (toAccountType == "BANK") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { toAccountType = "BANK" }
                            ) {
                                Text(
                                    "Bank",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (toAccountType == "BANK") FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (toAccountType == "INVESTMENT") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { toAccountType = "INVESTMENT" }
                            ) {
                                Text(
                                    "Investment",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (toAccountType == "INVESTMENT") FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        if (toAccountType == "BANK" && bankAccounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            BankDropdownPicker(
                                label = "To Bank",
                                bankAccounts = bankAccounts,
                                selectedBankId = toBankAccountId,
                                onSelectBank = { toBankAccountId = it }
                            )
                        }

                        if (toAccountType == "INVESTMENT" && investments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            InvestmentDropdownPicker(
                                investments = investments,
                                selectedInvestmentId = toInvestmentId,
                                onSelectInvestment = { toInvestmentId = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Note Field
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Note / Description (Optional)") },
                        placeholder = { Text("e.g., Monthly groceries at supermarket") },
                        singleLine = false,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transaction_note_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull()
                            if (amount == null || amount <= 0) {
                                errorMessage = "Please enter a valid amount greater than 0"
                                return@Button
                            }
                            if (accountType == "BANK" && selectedBankAccountId == null && bankAccounts.isEmpty()) {
                                errorMessage = "Please add a bank account first"
                                return@Button
                            }
                            if (currentType == "TRANSFER") {
                                if (accountType == "CASH" && toAccountType == "CASH") {
                                    errorMessage = "Cannot transfer from Cash to Cash"
                                    return@Button
                                }
                                if (accountType == "BANK" && toAccountType == "BANK" && selectedBankAccountId == toBankAccountId) {
                                    errorMessage = "Cannot transfer to the same bank account"
                                    return@Button
                                }
                            }

                            onSaveTransaction(
                                currentType,
                                amount,
                                selectedCategory,
                                accountType,
                                if (accountType == "BANK") selectedBankAccountId else null,
                                if (currentType == "TRANSFER") toAccountType else null,
                                if (currentType == "TRANSFER" && toAccountType == "BANK") toBankAccountId else null,
                                if (currentType == "TRANSFER" && toAccountType == "INVESTMENT") toInvestmentId else null,
                                System.currentTimeMillis(),
                                noteText.trim()
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (currentType) {
                                "CREDIT" -> CreditGreen
                                "TRANSFER" -> TransferBlue
                                else -> DebitRed
                            }
                        ),
                        modifier = Modifier.testTag("save_transaction_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Transaction")
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChipItem(
    category: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) category.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, category.color) else null,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("category_chip_${category.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.displayName,
                tint = if (isSelected) category.color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BankDropdownPicker(
    label: String,
    bankAccounts: List<BankAccountWithBalance>,
    selectedBankId: Long?,
    onSelectBank: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedBank = bankAccounts.find { it.account.id == selectedBankId } ?: bankAccounts.firstOrNull()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedBank?.let { "${it.account.bankName} (${it.account.accountNumber.takeLast(4)})" } ?: "Select Bank",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            bankAccounts.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text("${item.account.bankName} (${item.account.accountNumber})")
                    },
                    onClick = {
                        onSelectBank(item.account.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvestmentDropdownPicker(
    investments: List<InvestmentEntity>,
    selectedInvestmentId: Long?,
    onSelectInvestment: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedInv = investments.find { it.id == selectedInvestmentId } ?: investments.firstOrNull()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedInv?.let { "${it.name} (${it.type})" } ?: "Select Investment",
            onValueChange = {},
            readOnly = true,
            label = { Text("To Investment Asset") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            investments.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text("${item.name} (${item.type})")
                    },
                    onClick = {
                        onSelectInvestment(item.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
