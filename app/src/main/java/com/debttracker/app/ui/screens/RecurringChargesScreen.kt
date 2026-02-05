package com.debttracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debttracker.app.data.model.Person
import com.debttracker.app.data.model.RecurringCharge
import com.debttracker.app.data.model.TransactionType
import com.debttracker.app.ui.DebtTrackerViewModel
import com.debttracker.app.ui.components.*
import com.debttracker.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecurringChargesScreen(
    person: Person,
    viewModel: DebtTrackerViewModel,
    onBack: () -> Unit
) {
    val charges by viewModel.getRecurringChargesForPerson(person.id)
        .collectAsState(initial = emptyList())

    var deletingCharge by remember { mutableStateOf<RecurringCharge?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        MatrixHeader(
            title = "Recurring: ${person.name}",
            leadingContent = {
                MatrixIconButton(
                    icon = { Icon(Icons.Default.ArrowBack, contentDescription = "Back") },
                    onClick = onBack
                )
            }
        )

        if (charges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "> No recurring charges configured_",
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
                items(charges, key = { it.id }) { charge ->
                    RecurringChargeItem(
                        charge = charge,
                        onDelete = { deletingCharge = charge }
                    )
                }
            }
        }
    }

    deletingCharge?.let { charge ->
        ConfirmDialog(
            title = "Delete Recurring",
            message = "CONFIRM: Delete this recurring charge?",
            onConfirm = {
                viewModel.deleteRecurringCharge(charge)
                deletingCharge = null
            },
            onDismiss = { deletingCharge = null },
            isDanger = true
        )
    }
}

@Composable
fun RecurringChargeItem(
    charge: RecurringCharge,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    val frequencyText = when (charge.frequencyDays) {
        7 -> "Weekly"
        14 -> "Bi-weekly"
        30 -> "Monthly"
        else -> "Every ${charge.frequencyDays} days"
    }

    MatrixCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "> ${charge.description}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatrixGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$frequencyText | Next: ${dateFormat.format(Date(charge.nextDue))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixGreenDim
                )
            }

            val amountColor = if (charge.type == TransactionType.DEBT) MatrixGreen else MatrixRed
            val prefix = if (charge.type == TransactionType.DEBT) "+" else "-"

            Text(
                text = "$prefix$${String.format(Locale.US, "%.2f", charge.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            MatrixIconButton(
                icon = { Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp)) },
                onClick = onDelete,
                isDanger = true
            )
        }
    }
}
