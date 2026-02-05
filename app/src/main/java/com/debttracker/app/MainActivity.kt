package com.debttracker.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.debttracker.app.data.model.Person
import com.debttracker.app.data.model.TransactionType
import com.debttracker.app.ui.DebtTrackerViewModel
import com.debttracker.app.ui.screens.*
import com.debttracker.app.ui.theme.DebtTrackerTheme
import com.debttracker.app.ui.theme.MatrixBlack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebtTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MatrixBlack
                ) {
                    DebtTrackerApp()
                }
            }
        }
    }
}

enum class Screen {
    HOME,
    SETTINGS,
    TRANSACTION_HISTORY,
    RECURRING_CHARGES
}

@Composable
fun DebtTrackerApp() {
    val viewModel: DebtTrackerViewModel = viewModel()
    val context = androidx.compose.ui.platform.LocalContext.current

    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val selectedPerson by viewModel.selectedPerson.collectAsState()
    val recurringResult by viewModel.recurringProcessResult.collectAsState()

    // Dialog states
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showPersonActionsDialog by remember { mutableStateOf(false) }
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showDeletePersonDialog by remember { mutableStateOf(false) }

    // Toast messages
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Show recurring charges result
    LaunchedEffect(recurringResult) {
        recurringResult?.let { (applied, skipped) ->
            val message = if (skipped > 0) {
                "> Applied $applied recurring charge(s) ($skipped truncated)"
            } else if (applied > 0) {
                "> Applied $applied recurring charge(s)"
            } else null

            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            viewModel.clearRecurringResult()
        }
    }

    // Main screen content
    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                viewModel = viewModel,
                onPersonClick = { person ->
                    viewModel.selectPerson(person)
                    showPersonActionsDialog = true
                },
                onSettingsClick = { currentScreen = Screen.SETTINGS },
                onAddPersonClick = { showAddPersonDialog = true }
            )
        }
        Screen.SETTINGS -> {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { currentScreen = Screen.HOME }
            )
        }
        Screen.TRANSACTION_HISTORY -> {
            selectedPerson?.let { person ->
                TransactionHistoryScreen(
                    person = person,
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.HOME }
                )
            } ?: run { currentScreen = Screen.HOME }
        }
        Screen.RECURRING_CHARGES -> {
            selectedPerson?.let { person ->
                RecurringChargesScreen(
                    person = person,
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.HOME }
                )
            } ?: run { currentScreen = Screen.HOME }
        }
    }

    // Dialogs
    if (showAddPersonDialog) {
        AddPersonDialog(
            onDismiss = { showAddPersonDialog = false },
            onConfirm = { name ->
                viewModel.addPerson(name)
                showAddPersonDialog = false
            }
        )
    }

    if (showPersonActionsDialog) {
        selectedPerson?.let { person ->
            PersonActionsDialog(
                person = person,
                onDismiss = { showPersonActionsDialog = false },
                onAddDebt = {
                    showPersonActionsDialog = false
                    showAddDebtDialog = true
                },
                onAddPayment = {
                    showPersonActionsDialog = false
                    showAddPaymentDialog = true
                },
                onAddRecurring = {
                    showPersonActionsDialog = false
                    showAddRecurringDialog = true
                },
                onViewLog = {
                    showPersonActionsDialog = false
                    currentScreen = Screen.TRANSACTION_HISTORY
                },
                onManageRecurring = {
                    showPersonActionsDialog = false
                    currentScreen = Screen.RECURRING_CHARGES
                },
                onEditName = {
                    showPersonActionsDialog = false
                    showEditNameDialog = true
                },
                onDelete = {
                    showPersonActionsDialog = false
                    showDeletePersonDialog = true
                }
            )
        }
    }

    if (showAddDebtDialog) {
        selectedPerson?.let { person ->
            AddTransactionDialog(
                person = person,
                type = TransactionType.DEBT,
                onDismiss = { showAddDebtDialog = false },
                onConfirm = { amount, description ->
                    viewModel.addTransaction(person.id, amount, description, TransactionType.DEBT)
                    showAddDebtDialog = false
                }
            )
        }
    }

    if (showAddPaymentDialog) {
        selectedPerson?.let { person ->
            AddTransactionDialog(
                person = person,
                type = TransactionType.PAYMENT,
                onDismiss = { showAddPaymentDialog = false },
                onConfirm = { amount, description ->
                    viewModel.addTransaction(person.id, amount, description, TransactionType.PAYMENT)
                    showAddPaymentDialog = false
                }
            )
        }
    }

    if (showAddRecurringDialog) {
        selectedPerson?.let { person ->
            AddRecurringChargeDialog(
                person = person,
                onDismiss = { showAddRecurringDialog = false },
                onConfirm = { amount, description, type, days ->
                    viewModel.addRecurringCharge(person.id, amount, description, type, days)
                    showAddRecurringDialog = false
                }
            )
        }
    }

    if (showEditNameDialog) {
        selectedPerson?.let { person ->
            EditPersonNameDialog(
                person = person,
                onDismiss = { showEditNameDialog = false },
                onConfirm = { newName ->
                    viewModel.updatePersonName(person.id, newName)
                    showEditNameDialog = false
                }
            )
        }
    }

    if (showDeletePersonDialog) {
        selectedPerson?.let { person ->
            ConfirmDialog(
                title = "Terminate Entry",
                message = "CONFIRM: Delete ${person.name} and all associated records?",
                onConfirm = {
                    viewModel.deletePerson(person)
                    showDeletePersonDialog = false
                },
                onDismiss = { showDeletePersonDialog = false },
                isDanger = true
            )
        }
    }
}
