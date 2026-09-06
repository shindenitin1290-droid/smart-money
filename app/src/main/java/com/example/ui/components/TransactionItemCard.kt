package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.CreditGreen
import com.example.ui.theme.DebitRed
import com.example.ui.theme.SleekCreditGreen
import com.example.ui.theme.SleekCreditGreenContainer
import com.example.ui.theme.SleekDebitRed
import com.example.ui.theme.SleekDebitRedContainer
import com.example.ui.theme.SleekOnSecondaryContainer
import com.example.ui.theme.SleekOutlineVariant
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSurfaceCard
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekTransferBlue
import com.example.ui.theme.SleekTransferBlueContainer
import com.example.ui.theme.TransferBlue
import com.example.ui.viewmodel.BankAccountWithBalance
import com.example.ui.viewmodel.ExpenseTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    bankAccounts: List<BankAccountWithBalance>,
    currencySymbol: String,
    onDeleteClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCredit = transaction.type.equals("CREDIT", ignoreCase = true)
    val isTransfer = transaction.type.equals("TRANSFER", ignoreCase = true)

    val category = if (isTransfer) {
        null
    } else {
        CategoryRegistry.getCategory(transaction.category, isCredit = isCredit)
    }

    val accountLabel = getAccountLabel(
        accountType = transaction.accountType,
        bankAccountId = transaction.bankAccountId,
        toAccountType = transaction.toAccountType,
        toBankAccountId = transaction.toBankAccountId,
        isTransfer = isTransfer,
        bankAccounts = bankAccounts
    )

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(transaction.date))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SleekSurfaceCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = SleekOutlineVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            val badgeBg = when {
                isCredit -> SleekCreditGreenContainer
                isTransfer -> SleekTransferBlueContainer
                else -> SleekDebitRedContainer
            }
            val iconTint = when {
                isCredit -> SleekCreditGreen
                isTransfer -> SleekTransferBlue
                else -> SleekDebitRed
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                if (isTransfer) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Transfer",
                        tint = SleekTransferBlue,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = category?.icon ?: Icons.Default.SwapHoriz,
                        contentDescription = category?.displayName ?: "Transaction",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isTransfer) "Transfer" else category?.displayName ?: transaction.category,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = SleekTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = SleekTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SleekSecondaryContainer
                    ) {
                        Text(
                            text = accountLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = SleekOnSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = SleekTextSecondary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount and Delete Action
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val prefix = when {
                    isCredit -> "+"
                    isTransfer -> "⇄"
                    else -> "-"
                }
                val amountColor = when {
                    isCredit -> SleekCreditGreen
                    isTransfer -> SleekTransferBlue
                    else -> SleekDebitRed
                }

                Text(
                    text = "$prefix ${ExpenseTrackerViewModel.formatAmount(transaction.amount, currencySymbol)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = amountColor
                )

                IconButton(
                    onClick = { onDeleteClick(transaction) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_transaction_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction",
                        tint = SleekTextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun getAccountLabel(
    accountType: String,
    bankAccountId: Long?,
    toAccountType: String?,
    toBankAccountId: Long?,
    isTransfer: Boolean,
    bankAccounts: List<BankAccountWithBalance>
): String {
    fun findBankName(id: Long?): String {
        return bankAccounts.find { it.account.id == id }?.account?.bankName ?: "Bank"
    }

    return if (isTransfer) {
        val from = if (accountType.equals("CASH", ignoreCase = true)) "Cash" else findBankName(bankAccountId)
        val to = if (toAccountType.equals("CASH", ignoreCase = true)) "Cash"
        else if (toAccountType.equals("INVESTMENT", ignoreCase = true)) "Investment"
        else findBankName(toBankAccountId)
        "$from → $to"
    } else {
        if (accountType.equals("CASH", ignoreCase = true)) {
            "Cash"
        } else {
            val bank = bankAccounts.find { it.account.id == bankAccountId }
            if (bank != null) {
                "${bank.account.bankName} (${bank.account.accountNumber.takeLast(4)})"
            } else {
                "Bank Account"
            }
        }
    }
}
