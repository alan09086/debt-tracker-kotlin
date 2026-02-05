package com.debttracker.app.data.database

import androidx.room.*
import com.debttracker.app.data.model.Person
import com.debttracker.app.data.model.RecurringCharge
import com.debttracker.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getAllPersons(): Flow<List<Person>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: Long): Person?

    @Query("SELECT * FROM persons WHERE LOWER(name) = LOWER(:name)")
    suspend fun getPersonByName(name: String): Person?

    @Insert
    suspend fun insert(person: Person): Long

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("UPDATE persons SET balance = balance + :amount WHERE id = :personId")
    suspend fun updateBalance(personId: Long, amount: Double)

    @Query("DELETE FROM persons")
    suspend fun deleteAll()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE personId = :personId ORDER BY date DESC")
    fun getTransactionsForPerson(personId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE personId = :personId ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(personId: Long, limit: Int = 2): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Transaction>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface RecurringChargeDao {
    @Query("SELECT * FROM recurring_charges WHERE personId = :personId")
    fun getChargesForPerson(personId: Long): Flow<List<RecurringCharge>>

    @Query("SELECT * FROM recurring_charges WHERE nextDue <= :currentTime")
    suspend fun getDueCharges(currentTime: Long): List<RecurringCharge>

    @Query("SELECT * FROM recurring_charges")
    suspend fun getAllCharges(): List<RecurringCharge>

    @Insert
    suspend fun insert(charge: RecurringCharge): Long

    @Update
    suspend fun update(charge: RecurringCharge)

    @Delete
    suspend fun delete(charge: RecurringCharge)

    @Query("DELETE FROM recurring_charges")
    suspend fun deleteAll()
}
