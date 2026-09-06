package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.local.AppDatabase
import com.example.data.repository.ExpenseTrackerRepository
import com.example.ui.ExpenseTrackerApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ExpenseTrackerViewModel
import com.example.ui.viewmodel.ExpenseTrackerViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseTrackerViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ExpenseTrackerRepository(
            cashDao = database.cashDao(),
            bankAccountDao = database.bankAccountDao(),
            investmentDao = database.investmentDao(),
            transactionDao = database.transactionDao()
        )
        ExpenseTrackerViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseTrackerApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
