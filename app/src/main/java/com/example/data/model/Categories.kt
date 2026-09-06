package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TransactionType(val displayName: String) {
    CREDIT("Credit (Income)"),
    DEBIT("Debit (Expense)"),
    TRANSFER("Transfer")
}

enum class AccountType(val displayName: String) {
    CASH("Cash"),
    BANK("Bank Account")
}

enum class InvestmentType(val displayName: String) {
    STOCK("Stocks / Equity"),
    MUTUAL_FUND("Mutual Funds / SIP"),
    FIXED_DEPOSIT("Fixed Deposit (FD)"),
    OTHER("Other Investment")
}

data class CategoryItem(
    val id: String,
    val displayName: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryRegistry {
    val incomeCategories: List<CategoryItem> = listOf(
        CategoryItem("salary", "Salary", Icons.Default.Work, Color(0xFF10B981)),
        CategoryItem("interest", "Interest", Icons.Default.Savings, Color(0xFF0EA5E9)),
        CategoryItem("other_income", "Other Income", Icons.Default.Paid, Color(0xFF8B5CF6))
    )

    val expenseCategories: List<CategoryItem> = listOf(
        CategoryItem("grocery", "Grocery", Icons.Default.ShoppingCart, Color(0xFF10B981)),
        CategoryItem("milk", "Milk", Icons.Default.LocalDrink, Color(0xFF06B6D4)),
        CategoryItem("vegetable", "Vegetable", Icons.Default.Eco, Color(0xFF22C55E)),
        CategoryItem("fuel", "Fuel", Icons.Default.LocalGasStation, Color(0xFFF97316)),
        CategoryItem("vehicle_servicing", "Vehicle Servicing", Icons.Default.Build, Color(0xFF64748B)),
        CategoryItem("medical", "Medical", Icons.Default.LocalHospital, Color(0xFFEF4444)),
        CategoryItem("entertainment", "Entertainment", Icons.Default.Movie, Color(0xFFA855F7)),
        CategoryItem("cloth", "Cloth", Icons.Default.Checkroom, Color(0xFFEC4899)),
        CategoryItem("hotel", "Hotel & Dining", Icons.Default.Restaurant, Color(0xFFF59E0B)),
        CategoryItem("travel", "Travel", Icons.Default.Flight, Color(0xFF3B82F6)),
        CategoryItem("footwear", "Footwear", Icons.Default.DirectionsWalk, Color(0xFF84CC16)),
        CategoryItem("education", "Education", Icons.Default.School, Color(0xFF6366F1)),
        CategoryItem("investment", "Investment", Icons.Default.TrendingUp, Color(0xFF14B8A6)),
        CategoryItem("personal_care", "Personal Care", Icons.Default.Spa, Color(0xFFF43F5E)),
        CategoryItem("electricity", "Electricity", Icons.Default.Bolt, Color(0xFFEAB308)),
        CategoryItem("gas", "Gas", Icons.Default.LocalFireDepartment, Color(0xFFFB923C)),
        CategoryItem("donation", "Donation", Icons.Default.Favorite, Color(0xFFF472B6)),
        CategoryItem("tax", "Tax", Icons.Default.Receipt, Color(0xFF78716C)),
        CategoryItem("mobile", "Mobile", Icons.Default.PhoneAndroid, Color(0xFF0284C7)),
        CategoryItem("tv", "TV / Cable", Icons.Default.Tv, Color(0xFF7C3AED)),
        CategoryItem("insurance", "Insurance", Icons.Default.Security, Color(0xFF0D9488)),
        CategoryItem("home_decoration", "Home Decoration", Icons.Default.Weekend, Color(0xFFD97706)),
        CategoryItem("big_purchase", "Big Purchase", Icons.Default.ShoppingBag, Color(0xFFDC2626)),
        CategoryItem("other", "Other", Icons.Default.MoreHoriz, Color(0xFF94A3B8))
    )

    fun getCategory(id: String, isCredit: Boolean = false): CategoryItem {
        val list = if (isCredit) incomeCategories else expenseCategories
        return list.find { it.id.equals(id, ignoreCase = true) || it.displayName.equals(id, ignoreCase = true) }
            ?: if (isCredit) {
                CategoryItem(id, id, Icons.Default.Paid, Color(0xFF8B5CF6))
            } else {
                CategoryItem(id, id, Icons.Default.MoreHoriz, Color(0xFF94A3B8))
            }
    }
}
