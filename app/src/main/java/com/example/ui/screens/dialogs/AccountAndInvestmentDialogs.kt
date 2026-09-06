package com.example.ui.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.InvestmentEntity
import com.example.data.model.InvestmentType

@Composable
fun EditCashBalanceDialog(
    currentOpeningBalance: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(if (currentOpeningBalance > 0) currentOpeningBalance.toString() else "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_cash_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cash Opening Balance",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Enter the opening balance available in your physical cash wallet. All subsequent cash transactions will adjust this amount.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amountText = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Cash Opening Balance") },
                    leadingIcon = { Text(currencySymbol, modifier = Modifier.padding(start = 12.dp, end = 4.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cash_opening_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(20.dp))
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
                            if (amount == null || amount < 0) {
                                errorMessage = "Please enter a valid amount"
                                return@Button
                            }
                            onSave(amount)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_cash_opening_button")
                    ) {
                        Text("Save Balance")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditBankDialog(
    existingBank: BankAccountEntity? = null,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (name: String, number: String, openingBalance: Double, color: Long) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var bankName by remember { mutableStateOf(existingBank?.bankName ?: "") }
    var accountNumber by remember { mutableStateOf(existingBank?.accountNumber ?: "") }
    var openingBalanceText by remember {
        mutableStateOf(
            if (existingBank != null && existingBank.openingBalance > 0) existingBank.openingBalance.toString()
            else ""
        )
    }

    val colorOptions = listOf(
        0xFF0F766E, // Emerald Teal
        0xFF1D4ED8, // Deep Blue
        0xFF4338CA, // Indigo
        0xFF7E22CE, // Purple
        0xFFB45309, // Amber Brown
        0xFFBE123C, // Crimson
        0xFF047857  // Forest Green
    )
    var selectedColor by remember { mutableStateOf(existingBank?.colorHex ?: colorOptions.first()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_bank_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingBank == null) "Add Bank Account" else "Edit Bank Account",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = bankName,
                    onValueChange = {
                        bankName = it
                        errorMessage = null
                    },
                    label = { Text("Bank Name") },
                    placeholder = { Text("e.g., HDFC Bank, SBI, Chase") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number / Nickname") },
                    placeholder = { Text("e.g., •••• 4521 or Salary Acc") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_number_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = openingBalanceText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            openingBalanceText = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Opening Balance ($currencySymbol)") },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Text(currencySymbol, modifier = Modifier.padding(start = 12.dp, end = 4.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_opening_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Card Theme Color",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDelete != null) {
                        IconButton(
                            onClick = {
                                onDelete()
                                onDismiss()
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (bankName.isBlank()) {
                                    errorMessage = "Please enter bank name"
                                    return@Button
                                }
                                val opening = openingBalanceText.toDoubleOrNull() ?: 0.0
                                onSave(bankName.trim(), accountNumber.trim(), opening, selectedColor)
                                onDismiss()
                            },
                            modifier = Modifier.testTag("save_bank_button")
                        ) {
                            Text("Save Account")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInvestmentDialog(
    existingInvestment: InvestmentEntity? = null,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        type: String,
        openingBalance: Double,
        currentBalance: Double,
        institution: String,
        notes: String
    ) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(existingInvestment?.name ?: "") }
    var selectedType by remember { mutableStateOf(existingInvestment?.type ?: "STOCK") }
    var openingBalanceText by remember {
        mutableStateOf(
            if (existingInvestment != null && existingInvestment.openingBalance > 0) existingInvestment.openingBalance.toString()
            else ""
        )
    }
    var currentBalanceText by remember {
        mutableStateOf(
            if (existingInvestment != null && existingInvestment.currentBalance > 0) existingInvestment.currentBalance.toString()
            else ""
        )
    }
    var institution by remember { mutableStateOf(existingInvestment?.institution ?: "") }
    var notes by remember { mutableStateOf(existingInvestment?.notes ?: "") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_investment_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingInvestment == null) "Add Investment" else "Edit Investment",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Investment Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val displayType = when (selectedType) {
                        "STOCK" -> "Stocks / Equity"
                        "MUTUAL_FUND" -> "Mutual Funds / SIP"
                        "FIXED_DEPOSIT" -> "Fixed Deposit (FD)"
                        else -> "Other Investment"
                    }

                    OutlinedTextField(
                        value = displayType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Investment Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Stocks / Equity") },
                            onClick = {
                                selectedType = "STOCK"
                                typeDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mutual Funds / SIP") },
                            onClick = {
                                selectedType = "MUTUAL_FUND"
                                typeDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Fixed Deposit (FD)") },
                            onClick = {
                                selectedType = "FIXED_DEPOSIT"
                                typeDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Other Investment (Gold, PPF, Real Estate)") },
                            onClick = {
                                selectedType = "OTHER"
                                typeDropdownExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Investment Name") },
                    placeholder = {
                        when (selectedType) {
                            "STOCK" -> Text("e.g., Apple, Reliance Industries, Tesla")
                            "MUTUAL_FUND" -> Text("e.g., Nifty 50 Index Fund, Vanguard")
                            "FIXED_DEPOSIT" -> Text("e.g., HDFC 1-Year FD 7.2%")
                            else -> Text("e.g., Gold Sovereign, PPF Account")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("investment_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = openingBalanceText,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                openingBalanceText = it
                                if (currentBalanceText.isBlank()) {
                                    currentBalanceText = it
                                }
                                errorMessage = null
                            }
                        },
                        label = { Text("Invested / Opening") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("investment_opening_input")
                    )

                    OutlinedTextField(
                        value = currentBalanceText,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                currentBalanceText = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Current Value") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("investment_current_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Broker / Platform / Bank") },
                    placeholder = { Text("e.g., Zerodha, Groww, Vanguard, SBI") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDelete != null) {
                        IconButton(
                            onClick = {
                                onDelete()
                                onDismiss()
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "Please enter investment name"
                                    return@Button
                                }
                                val opening = openingBalanceText.toDoubleOrNull()
                                if (opening == null || opening < 0) {
                                    errorMessage = "Please enter valid opening/invested balance"
                                    return@Button
                                }
                                val current = currentBalanceText.toDoubleOrNull() ?: opening
                                onSave(name.trim(), selectedType, opening, current, institution.trim(), notes.trim())
                                onDismiss()
                            },
                            modifier = Modifier.testTag("save_investment_button")
                        ) {
                            Text("Save Investment")
                        }
                    }
                }
            }
        }
    }
}
