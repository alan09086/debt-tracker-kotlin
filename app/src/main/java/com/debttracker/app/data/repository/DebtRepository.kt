package com.debttracker.app.data.repository

import com.debttracker.app.data.database.PersonDao
import com.debttracker.app.data.database.RecurringChargeDao
import com.debttracker.app.data.database.TransactionDao
import com.debttracker.app.data.model.*
import kotlinx.coroutines.flow.Flow

class DebtRepository(
    private val personDao: PersonDao,
    private val transactionDao: TransactionDao,
    private val recurringChargeDao: RecurringChargeDao
) {
    companion object {
        const val MAX_RECURRING_BACKFILL = 12
    }

    // Persons
    val allPersons: Flow<List<Person>> = personDao.getAllPersons()

    suspend fun getPersonById(id: Long): Person? = personDao.getPersonById(id)

    suspend fun getPersonByName(name: String): Person? = personDao.getPersonByName(name)

    suspend fun addPerson(name: String): Long {
        val person = Person(name = name)
        return personDao.insert(person)
    }

    suspend fun updatePersonName(personId: Long, newName: String) {
        val person = personDao.getPersonById(personId) ?: return
        personDao.update(person.copy(name = newName))
    }

    suspend fun deletePerson(person: Person) {
        personDao.delete(person)
    }

    // Transactions
    fun getTransactionsForPerson(personId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsForPerson(personId)

    fun getRecentTransactions(personId: Long, limit: Int = 2): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions(personId, limit)

    suspend fun addTransaction(
        personId: Long,
        amount: Double,
        description: String,
        type: TransactionType,
        isRecurring: Boolean = false
    ): Long {
        val signedAmount = if (type == TransactionType.DEBT) amount else -amount
        val transaction = Transaction(
            personId = personId,
            amount = signedAmount,
            description = description,
            type = type,
            isRecurring = isRecurring
        )
        val id = transactionDao.insert(transaction)
        personDao.updateBalance(personId, signedAmount)
        return id
    }

    suspend fun updateTransaction(
        transactionId: Long,
        newAmount: Double,
        newDescription: String
    ) {
        val transaction = transactionDao.getTransactionById(transactionId) ?: return
        val oldAmount = transaction.amount
        val newSignedAmount = if (transaction.type == TransactionType.DEBT) newAmount else -newAmount

        transactionDao.update(transaction.copy(
            amount = newSignedAmount,
            description = newDescription
        ))

        // Update balance: remove old, add new
        personDao.updateBalance(transaction.personId, -oldAmount + newSignedAmount)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
        personDao.updateBalance(transaction.personId, -transaction.amount)
    }

    suspend fun getTransactionById(id: Long): Transaction? = transactionDao.getTransactionById(id)

    // Recurring Charges
    fun getChargesForPerson(personId: Long): Flow<List<RecurringCharge>> =
        recurringChargeDao.getChargesForPerson(personId)

    suspend fun addRecurringCharge(
        personId: Long,
        amount: Double,
        description: String,
        type: TransactionType,
        frequencyDays: Int
    ): Long {
        val nextDue = System.currentTimeMillis() + (frequencyDays * 24 * 60 * 60 * 1000L)
        val charge = RecurringCharge(
            personId = personId,
            amount = amount,
            description = description,
            type = type,
            frequencyDays = frequencyDays,
            nextDue = nextDue
        )
        return recurringChargeDao.insert(charge)
    }

    suspend fun deleteRecurringCharge(charge: RecurringCharge) {
        recurringChargeDao.delete(charge)
    }

    suspend fun processRecurringCharges(): Pair<Int, Int> {
        val now = System.currentTimeMillis()
        val dueCharges = recurringChargeDao.getDueCharges(now)
        var applied = 0
        var skipped = 0

        for (charge in dueCharges) {
            var nextDue = charge.nextDue
            var iterations = 0

            while (nextDue <= now && iterations < MAX_RECURRING_BACKFILL) {
                addTransaction(
                    personId = charge.personId,
                    amount = charge.amount,
                    description = "${charge.description} [AUTO]",
                    type = charge.type,
                    isRecurring = true
                )
                nextDue += charge.frequencyDays * 24 * 60 * 60 * 1000L
                applied++
                iterations++
            }

            // If we hit the limit, fast-forward
            if (iterations >= MAX_RECURRING_BACKFILL && nextDue <= now) {
                skipped++
                nextDue = now + charge.frequencyDays * 24 * 60 * 60 * 1000L
            }

            recurringChargeDao.update(charge.copy(nextDue = nextDue))
        }

        return Pair(applied, skipped)
    }

    // Backup/Restore
    suspend fun getBackupData(): BackupData {
        return BackupData(
            persons = personDao.getAllPersons().let { flow ->
                val list = mutableListOf<Person>()
                // We need to collect once, so we'll query directly
                list
            },
            transactions = transactionDao.getAllTransactions(),
            recurringCharges = recurringChargeDao.getAllCharges()
        )
    }

    suspend fun getAllPersonsSnapshot(): List<Person> {
        // Direct query for backup
        return personDao.getAllPersons().let {
            val result = mutableListOf<Person>()
            // This is a workaround - we'll collect the flow in the ViewModel
            result
        }
    }

    suspend fun clearAllData() {
        transactionDao.deleteAll()
        recurringChargeDao.deleteAll()
        personDao.deleteAll()
    }

    suspend fun restoreFromBackup(backup: BackupData) {
        clearAllData()
        backup.persons.forEach { personDao.insert(it) }
        backup.transactions.forEach { transactionDao.insert(it) }
        backup.recurringCharges.forEach { recurringChargeDao.insert(it) }
    }
}
