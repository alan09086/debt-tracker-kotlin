package com.debttracker.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.debttracker.app.data.model.Person
import com.debttracker.app.data.model.RecurringCharge
import com.debttracker.app.data.model.Transaction

@Database(
    entities = [Person::class, Transaction::class, RecurringCharge::class],
    version = 1,
    exportSchema = true
)
abstract class DebtTrackerDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun transactionDao(): TransactionDao
    abstract fun recurringChargeDao(): RecurringChargeDao

    companion object {
        @Volatile
        private var INSTANCE: DebtTrackerDatabase? = null

        fun getDatabase(context: Context): DebtTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DebtTrackerDatabase::class.java,
                    "debt_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
