package com.debttracker.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.debttracker.app.data.database.DebtTrackerDatabase
import com.debttracker.app.data.model.*
import com.debttracker.app.data.repository.DebtRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DebtTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DebtTrackerDatabase.getDatabase(application)
    private val repository = DebtRepository(
        database.personDao(),
        database.transactionDao(),
        database.recurringChargeDao()
    )

    val persons: StateFlow<List<Person>> = repository.allPersons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPerson = MutableStateFlow<Person?>(null)
    val selectedPerson: StateFlow<Person?> = _selectedPerson.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private val _recurringProcessResult = MutableStateFlow<Pair<Int, Int>?>(null)
    val recurringProcessResult: StateFlow<Pair<Int, Int>?> = _recurringProcessResult.asStateFlow()

    init {
        // Process recurring charges on startup
        viewModelScope.launch {
            val result = repository.processRecurringCharges()
            if (result.first > 0 || result.second > 0) {
                _recurringProcessResult.value = result
            }
        }
    }

    fun selectPerson(person: Person?) {
        _selectedPerson.value = person
    }

    fun getTransactionsForPerson(personId: Long): Flow<List<Transaction>> =
        repository.getTransactionsForPerson(personId)

    fun getRecentTransactions(personId: Long): Flow<List<Transaction>> =
        repository.getRecentTransactions(personId)

    fun getRecurringChargesForPerson(personId: Long): Flow<List<RecurringCharge>> =
        repository.getChargesForPerson(personId)

    fun addPerson(name: String) {
        viewModelScope.launch {
            val existing = repository.getPersonByName(name)
            if (existing != null) {
                _toastMessage.emit("ERROR: Entry already exists")
                return@launch
            }
            repository.addPerson(name)
            _toastMessage.emit("> Entry created")
        }
    }

    fun updatePersonName(personId: Long, newName: String) {
        viewModelScope.launch {
            val existing = repository.getPersonByName(newName)
            val currentPerson = repository.getPersonById(personId)
            if (existing != null && existing.id != personId) {
                _toastMessage.emit("ERROR: Entry already exists")
                return@launch
            }
            repository.updatePersonName(personId, newName)
            _selectedPerson.value = currentPerson?.copy(name = newName)
            _toastMessage.emit("> Name updated")
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch {
            repository.deletePerson(person)
            _selectedPerson.value = null
            _toastMessage.emit("> Entry terminated")
        }
    }

    fun addTransaction(
        personId: Long,
        amount: Double,
        description: String,
        type: TransactionType
    ) {
        viewModelScope.launch {
            repository.addTransaction(personId, amount, description, type)
            // Refresh selected person
            _selectedPerson.value = repository.getPersonById(personId)
            _toastMessage.emit("> Transaction recorded")
        }
    }

    fun updateTransaction(transactionId: Long, newAmount: Double, newDescription: String) {
        viewModelScope.launch {
            val transaction = repository.getTransactionById(transactionId)
            repository.updateTransaction(transactionId, newAmount, newDescription)
            if (transaction != null) {
                _selectedPerson.value = repository.getPersonById(transaction.personId)
            }
            _toastMessage.emit("> Transaction updated")
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _selectedPerson.value = repository.getPersonById(transaction.personId)
            _toastMessage.emit("> Transaction deleted")
        }
    }

    fun addRecurringCharge(
        personId: Long,
        amount: Double,
        description: String,
        type: TransactionType,
        frequencyDays: Int
    ) {
        viewModelScope.launch {
            repository.addRecurringCharge(personId, amount, description, type, frequencyDays)
            _toastMessage.emit("> Recurring charge configured")
        }
    }

    fun deleteRecurringCharge(charge: RecurringCharge) {
        viewModelScope.launch {
            repository.deleteRecurringCharge(charge)
            _toastMessage.emit("> Recurring charge deleted")
        }
    }

    fun clearRecurringResult() {
        _recurringProcessResult.value = null
    }

    // Backup functions
    suspend fun getBackupJsonAsync(): String {
        val gson = Gson()
        val persons = database.personDao().getAllPersons().first()
        val transactions = database.transactionDao().getAllTransactions()
        val recurring = database.recurringChargeDao().getAllCharges()

        val backup = BackupData(
            persons = persons,
            transactions = transactions,
            recurringCharges = recurring
        )
        return gson.toJson(backup)
    }

    fun restoreFromJson(json: String) {
        viewModelScope.launch {
            try {
                val gson = Gson()
                val backup = gson.fromJson(json, BackupData::class.java)
                repository.restoreFromBackup(backup)
                _toastMessage.emit("> Import successful")
            } catch (e: Exception) {
                _toastMessage.emit("ERROR: Invalid backup file")
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedPerson.value = null
            _toastMessage.emit("> System purged")
        }
    }
}
