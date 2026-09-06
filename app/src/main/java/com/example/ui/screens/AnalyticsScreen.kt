package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.CreditGreen
import com.example.ui.theme.DebitRed
import com.example.ui.theme.SleekCreditGreen
import com.example.ui.theme.SleekCreditGreenContainer
import com.example.ui.theme.SleekDebitRed
import com.example.ui.theme.SleekDebitRedContainer
import com.example.ui.theme.SleekOutlineVariant
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSubCard
import com.example.ui.theme.SleekSurfaceCard
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.viewmodel.ExpenseByCategoryItem
import com.example.ui.viewmodel.ExpenseTrackerUiState
import com.example.ui.viewmodel.ExpenseTrackerViewModel

@Composable
fun AnalyticsScreen(
    uiState: ExpenseTrackerUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(bottom = 96.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cash Flow Summary Card (Sleek styled)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cash_flow_summary_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SleekSurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = SleekOutlineVariant,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Cash Flow Overview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekTextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Income (Credits)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = SleekTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ExpenseTrackerViewModel.formatAmount(uiState.totalIncome, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SleekCreditGreen
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Expenses (Debits)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = SleekTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ExpenseTrackerViewModel.formatAmount(uiState.totalExpense, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SleekDebitRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Savings Ratio Bar
                    val totalTurnover = uiState.totalIncome + uiState.totalExpense
                    val incomeRatio = if (totalTurnover > 0) (uiState.totalIncome / totalTurnover).toFloat() else 0.5f

                    LinearProgressIndicator(
                        progress = { incomeRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SleekCreditGreen,
                        trackColor = SleekDebitRedContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Net Savings: ${ExpenseTrackerViewModel.formatAmount(uiState.netSavings, uiState.currencySymbol)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (uiState.netSavings >= 0) SleekCreditGreen else SleekDebitRed
                        )

                        val savingsRate = if (uiState.totalIncome > 0) ((uiState.netSavings / uiState.totalIncome) * 100) else 0.0
                        Text(
                            text = "Savings Rate: ${String.format("%.1f", savingsRate)}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = SleekTextSecondary
                        )
                    }
                }
            }
        }

        // Category Expenses Breakdown
        item {
            Text(
                text = "Expenditure by Category",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SleekTextPrimary
            )
        }

        if (uiState.expenseByCategory.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Expenditure Data Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SleekTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add debit expenses across grocery, fuel, medical, education, etc. to see spending analytics.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekTextSecondary
                        )
                    }
                }
            }
        } else {
            items(uiState.expenseByCategory, key = { it.categoryId }) { catExpense ->
                CategoryExpenseProgressCard(
                    item = catExpense,
                    currencySymbol = uiState.currencySymbol
                )
            }
        }
    }
}

@Composable
fun CategoryExpenseProgressCard(
    item: ExpenseByCategoryItem,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val category = CategoryRegistry.getCategory(item.categoryId, isCredit = false)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_expense_card_${item.categoryId}"),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SleekSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.displayName,
                        tint = SleekPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekTextPrimary
                    )
                    Text(
                        text = "${item.transactionCount} transaction${if (item.transactionCount > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SleekTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = ExpenseTrackerViewModel.formatAmount(item.totalAmount, currencySymbol),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekTextPrimary
                    )
                    Text(
                        text = "${String.format("%.1f", item.percentage)}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SleekPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (item.percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SleekPrimary,
                trackColor = SleekSecondaryContainer
            )
        }
    }
}
