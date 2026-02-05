package com.debttracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debttracker.app.data.model.Person
import com.debttracker.app.data.model.Transaction
import com.debttracker.app.ui.DebtTrackerViewModel
import com.debttracker.app.ui.components.*
import com.debttracker.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(
    person: Person,
    viewModel: DebtTrackerViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.getTransactionsForPerson(person.id)
        .collectAsState(initial = emptyList())

    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var deletingTransaction by remember { mutableStateOf<Transaction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header
        MatrixHeader(
            title = "Log: ${person.name}",
            leadingContent = {
                MatrixIconButton(
                    icon = { Icon(Icons.Default.ArrowBack, contentDescription = "Back") },
                    onClick = onBack
                )
            }
        )

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "> No transactions recorded_",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MatrixGreenDim,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onEdit = { editingTransaction = transaction },
                        onDelete = { deletingTransaction = transaction }
                    )
                }
            }
        }
    }

    // Edit dialog
    editingTransaction?.let { transaction ->
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { editingTransaction = null },
            onConfirm = { amount, description ->
                viewModel.updateTransaction(transaction.id, amount, description)
                editingTransaction = null
            }
        )
    }

    // Delete confirmation
    deletingTransaction?.let { transaction ->
        ConfirmDialog(
            title = "Delete Transaction",
            message = "CONFIRM: Delete this transaction?",
            onConfirm = {
                viewModel.deleteTransaction(transaction)
                deletingTransaction = null
            },
            onDismiss = { deletingTransaction = null },
            isDanger = true
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    MatrixCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "> ${transaction.description}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatrixGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixGreenDim
                )
            }

            Text(
                text = if (transaction.amount > 0) {
                    "+$${String.format("%.2f", transaction.amount)}"
                } else {
                    "-$${String.format("%.2f", kotlin.math.abs(transaction.amount))}"
                },
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.amount > 0) MatrixGreen else MatrixRed,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MatrixIconButton(
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp)) },
                    onClick = onEdit
                )
                MatrixIconButton(
                    icon = { Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp)) },
                    onClick = onDelete,
                    isDanger = true
                )
            }
        }
    }
}
