package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.daos.BankAccountDao
import com.example.data.local.daos.CashDao
import com.example.data.local.daos.InvestmentDao
import com.example.data.local.daos.TransactionDao
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.CashProfileEntity
import com.example.data.local.entities.InvestmentEntity
import com.example.data.local.entities.TransactionEntity

@Database(
    entities = [
        CashProfileEntity::class,
        BankAccountEntity::class,
        InvestmentEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cashDao(): CashDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
