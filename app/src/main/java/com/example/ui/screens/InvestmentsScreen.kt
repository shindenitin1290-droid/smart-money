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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.InvestmentEntity
import com.example.ui.theme.CreditGreen
import com.example.ui.theme.DebitRed
import com.example.ui.theme.SleekAccentPill
import com.example.ui.theme.SleekCreditGreen
import com.example.ui.theme.SleekCreditGreenContainer
import com.example.ui.theme.SleekDebitRed
import com.example.ui.theme.SleekDebitRedContainer
import com.example.ui.theme.SleekOnPrimaryContainer
import com.example.ui.theme.SleekOnSecondaryContainer
import com.example.ui.theme.SleekOutlineVariant
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSubCard
import com.example.ui.theme.SleekSurfaceCard
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.viewmodel.ExpenseTrackerUiState
import com.example.ui.viewmodel.ExpenseTrackerViewModel

@Composable
fun InvestmentsScreen(
    uiState: ExpenseTrackerUiState,
    onAddInvestmentClick: () -> Unit,
    onEditInvestmentClick: (InvestmentEntity) -> Unit,
    onDeleteInvestmentClick: (InvestmentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val filteredList = if (selectedCategoryFilter == "ALL") {
        uiState.investments
    } else {
        uiState.investments.filter { it.type.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("investments_screen"),
        contentPadding = PaddingValues(bottom = 96.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Portfolio Overview Hero Card (Sleek lavender theme)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("investment_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PORTFOLIO VALUE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekOnPrimaryContainer.copy(alpha = 0.75f)
                            )
                        )

                        // Returns Badge
                        val returns = uiState.totalInvestmentReturns
                        val returnPercent = if (uiState.totalInvestmentInvested > 0) {
                            (returns / uiState.totalInvestmentInvested) * 100
                        } else 0.0

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (returns >= 0) SleekCreditGreenContainer else SleekDebitRedContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (returns >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (returns >= 0) SleekCreditGreen else SleekDebitRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${if (returns >= 0) "+" else ""}${String.format("%.1f", returnPercent)}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (returns >= 0) SleekCreditGreen else SleekDebitRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ExpenseTrackerViewModel.formatAmount(uiState.totalInvestmentCurrent, uiState.currencySymbol),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = SleekOnPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = "Total Invested",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SleekOnPrimaryContainer.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = ExpenseTrackerViewModel.formatAmount(uiState.totalInvestmentInvested, uiState.currencySymbol),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SleekOnPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Net Gain / Loss",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SleekOnPrimaryContainer.copy(alpha = 0.75f)
                                )
                                val sign = if (uiState.totalInvestmentReturns >= 0) "+" else ""
                                Text(
                                    text = "$sign${ExpenseTrackerViewModel.formatAmount(uiState.totalInvestmentReturns, uiState.currencySymbol)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (uiState.totalInvestmentReturns >= 0) SleekCreditGreen else SleekDebitRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter and Add Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asset Holdings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekTextPrimary
                    )

                    Button(
                        onClick = onAddInvestmentClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        modifier = Modifier.testTag("add_investment_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Asset", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Asset Type Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterChips = listOf(
                        "ALL" to "All (${uiState.investments.size})",
                        "STOCK" to "Stocks",
                        "MUTUAL_FUND" to "Mutual Funds",
                        "FIXED_DEPOSIT" to "FD",
                        "OTHER" to "Other"
                    )

                    items(filterChips) { (key, label) ->
                        val isSelected = selectedCategoryFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = key },
                            shape = RoundedCornerShape(12.dp),
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }

        // List of Investments or Empty State
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .testTag("empty_investments_state"),
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(SleekSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Investment Assets Added",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SleekTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Track your Stocks, Mutual Funds, Fixed Deposits (FD), and Gold/PPF investments with opening and current balances.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                InvestmentItemCard(
                    investment = item,
                    currencySymbol = uiState.currencySymbol,
                    onEditClick = { onEditInvestmentClick(item) },
                    onDeleteClick = { onDeleteInvestmentClick(item) }
                )
            }
        }
    }
}

@Composable
fun InvestmentItemCard(
    investment: InvestmentEntity,
    currencySymbol: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = when (investment.type) {
        "STOCK" -> SleekCreditGreen
        "MUTUAL_FUND" -> SleekPrimary
        "FIXED_DEPOSIT" -> Color(0xFFD97706) // Amber
        else -> Color(0xFF7C3AED)
    }

    val typeContainer = when (investment.type) {
        "STOCK" -> SleekCreditGreenContainer
        "MUTUAL_FUND" -> SleekSecondaryContainer
        "FIXED_DEPOSIT" -> Color(0xFFFEF3C7)
        else -> Color(0xFFEDE9FE)
    }

    val typeLabel = when (investment.type) {
        "STOCK" -> "Stock"
        "MUTUAL_FUND" -> "Mutual Fund"
        "FIXED_DEPOSIT" -> "Fixed Deposit"
        else -> "Other"
    }

    val gain = investment.currentBalance - investment.openingBalance
    val gainPercent = if (investment.openingBalance > 0) (gain / investment.openingBalance) * 100 else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("investment_card_${investment.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = SleekOutlineVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = typeContainer
                    ) {
                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = typeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (investment.institution.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${investment.institution}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SleekTextSecondary
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SleekTextSecondary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = SleekDebitRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = investment.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SleekTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Invested / Opening",
                        style = MaterialTheme.typography.labelSmall,
                        color = SleekTextSecondary
                    )
                    Text(
                        text = ExpenseTrackerViewModel.formatAmount(investment.openingBalance, currencySymbol),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SleekTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Current Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = SleekTextSecondary
                    )
                    Text(
                        text = ExpenseTrackerViewModel.formatAmount(investment.currentBalance, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekTextPrimary
                    )
                    Text(
                        text = "${if (gain >= 0) "+" else ""}${ExpenseTrackerViewModel.formatAmount(gain, currencySymbol)} (${String.format("%.1f", gainPercent)}%)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (gain >= 0) SleekCreditGreen else SleekDebitRed
                    )
                }
            }
        }
    }
}
