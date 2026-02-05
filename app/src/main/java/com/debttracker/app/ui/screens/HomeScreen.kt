package com.debttracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: DebtTrackerViewModel,
    onPersonClick: (Person) -> Unit,
    onSettingsClick: () -> Unit,
    onAddPersonClick: () -> Unit
) {
    val persons by viewModel.persons.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header
        MatrixHeader(
            title = "DEBT_TRACKER",
            leadingContent = {
                MatrixIconButton(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    onClick = onSettingsClick
                )
            },
            trailingContent = {
                MatrixIconButton(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Person") },
                    onClick = onAddPersonClick
                )
            }
        )

        // Content
        if (persons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "> No records found. Press [+] to add entry_",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(persons, key = { it.id }) { person ->
                    PersonCard(
                        person = person,
                        viewModel = viewModel,
                        onClick = { onPersonClick(person) }
                    )
                }
            }
        }
    }
}

@Composable
fun PersonCard(
    person: Person,
    viewModel: DebtTrackerViewModel,
    onClick: () -> Unit
) {
    val recentTransactions by viewModel.getRecentTransactions(person.id)
        .collectAsState(initial = emptyList())

    val accentColor = when {
        person.balance > 0 -> MatrixGreen
        person.balance < 0 -> MatrixRed
        else -> MatrixGreenBorder
    }

    MatrixCard(
        onClick = onClick,
        accentColor = accentColor,
        borderColor = if (person.balance != 0.0) accentColor.copy(alpha = 0.5f) else MatrixGreenBorder
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = person.name.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MatrixGreen
            )
            BalanceBadge(balance = person.balance)
        }

        // Recent transactions preview
        if (recentTransactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            MatrixDivider()
            Spacer(modifier = Modifier.height(8.dp))

            recentTransactions.forEach { transaction ->
                TransactionPreviewItem(transaction = transaction)
            }
        }
    }
}

@Composable
fun TransactionPreviewItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "> ${transaction.description}",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixGreenDark,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (transaction.amount > 0) {
                "+$${String.format(Locale.US, "%.2f", transaction.amount)}"
            } else {
                "-$${String.format(Locale.US, "%.2f", kotlin.math.abs(transaction.amount))}"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (transaction.amount > 0) MatrixGreen else MatrixRed
        )
    }
}
