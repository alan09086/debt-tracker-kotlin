package com.debttracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.debttracker.app.data.model.Person
import com.debttracker.app.data.model.RecurringCharge
import com.debttracker.app.data.model.Transaction
import com.debttracker.app.data.model.TransactionType
import com.debttracker.app.ui.components.*
import com.debttracker.app.ui.theme.*
import java.util.Locale

@Composable
fun AddPersonDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    MatrixDialog(
        title = "New Entry",
        onDismiss = onDismiss
    ) {
        MatrixTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MatrixButton(
            text = "Execute",
            onClick = {
                if (name.isNotBlank()) {
                    onConfirm(name.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditPersonNameDialog(
    person: Person,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(person.name) }

    MatrixDialog(
        title = "Edit Name",
        onDismiss = onDismiss
    ) {
        MatrixTextField(
            value = name,
            onValueChange = { name = it },
            label = "New Name"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MatrixButton(
            text = "Update",
            onClick = {
                if (name.isNotBlank()) {
                    onConfirm(name.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddTransactionDialog(
    person: Person,
    type: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val title = if (type == TransactionType.DEBT) "Debt: ${person.name}" else "Payment: ${person.name}"

    MatrixDialog(
        title = title,
        onDismiss = onDismiss
    ) {
        MatrixTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Amount",
            keyboardType = KeyboardType.Decimal
        )
        Spacer(modifier = Modifier.height(12.dp))
        MatrixTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MatrixButton(
            text = "Confirm",
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null && parsedAmount > 0 && description.isNotBlank()) {
                    onConfirm(parsedAmount, description.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf(String.format(Locale.US, "%.2f", kotlin.math.abs(transaction.amount))) }
    var description by remember { mutableStateOf(transaction.description) }

    MatrixDialog(
        title = "Edit Transaction",
        onDismiss = onDismiss
    ) {
        MatrixTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Amount",
            keyboardType = KeyboardType.Decimal
        )
        Spacer(modifier = Modifier.height(12.dp))
        MatrixTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MatrixButton(
            text = "Update",
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null && parsedAmount > 0 && description.isNotBlank()) {
                    onConfirm(parsedAmount, description.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddRecurringChargeDialog(
    person: Person,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, TransactionType, Int) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.DEBT) }
    var frequency by remember { mutableStateOf("30") }
    var customDays by remember { mutableStateOf("") }
    var showCustom by remember { mutableStateOf(false) }

    MatrixDialog(
        title = "Recurring: ${person.name}",
        onDismiss = onDismiss
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            MatrixTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = "Amount",
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(12.dp))
            MatrixTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description"
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Type selection
            Text("> Type", style = MaterialTheme.typography.labelMedium, color = MatrixGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MatrixChip(
                    selected = type == TransactionType.DEBT,
                    onClick = { type = TransactionType.DEBT },
                    label = "They owe me"
                )
                MatrixChip(
                    selected = type == TransactionType.PAYMENT,
                    onClick = { type = TransactionType.PAYMENT },
                    label = "I owe them"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Frequency selection
            Text("> Frequency", style = MaterialTheme.typography.labelMedium, color = MatrixGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "7" to "Weekly (7 days)",
                    "14" to "Bi-weekly (14 days)",
                    "30" to "Monthly (30 days)",
                    "custom" to "Custom..."
                ).forEach { (value, label) ->
                    MatrixChip(
                        selected = if (value == "custom") showCustom else frequency == value && !showCustom,
                        onClick = {
                            if (value == "custom") {
                                showCustom = true
                            } else {
                                showCustom = false
                                frequency = value
                            }
                        },
                        label = label,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (showCustom) {
                Spacer(modifier = Modifier.height(8.dp))
                MatrixTextField(
                    value = customDays,
                    onValueChange = { customDays = it.filter { c -> c.isDigit() } },
                    label = "Custom Days",
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            MatrixButton(
                text = "Setup Recurring",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    val days = if (showCustom) customDays.toIntOrNull() else frequency.toIntOrNull()
                    if (parsedAmount != null && parsedAmount > 0 && description.isNotBlank() && days != null && days > 0) {
                        onConfirm(parsedAmount, description.trim(), type, days)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PersonActionsDialog(
    person: Person,
    onDismiss: () -> Unit,
    onAddDebt: () -> Unit,
    onAddPayment: () -> Unit,
    onAddRecurring: () -> Unit,
    onViewLog: () -> Unit,
    onManageRecurring: () -> Unit,
    onEditName: () -> Unit,
    onDelete: () -> Unit
) {
    MatrixDialog(
        title = person.name.uppercase(),
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ActionItem(
                icon = { Text("+$", style = MaterialTheme.typography.titleLarge, color = MatrixGreen) },
                title = "Add Debt",
                subtitle = "> They owe you credits",
                onClick = onAddDebt
            )
            ActionItem(
                icon = { Text("-$", style = MaterialTheme.typography.titleLarge, color = MatrixGreen) },
                title = "Add Payment",
                subtitle = "> You owe them or received payment",
                onClick = onAddPayment
            )
            ActionItem(
                icon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = MatrixGreen) },
                title = "Recurring Charge",
                subtitle = "> Setup automatic recurring debt",
                onClick = onAddRecurring
            )
            ActionItem(
                icon = { Text("[#]", style = MaterialTheme.typography.titleMedium, color = MatrixGreen) },
                title = "View Log",
                subtitle = "> Access transaction history",
                onClick = onViewLog
            )
            ActionItem(
                icon = { Icon(Icons.Default.List, contentDescription = null, tint = MatrixGreen) },
                title = "Manage Recurring",
                subtitle = "> View/delete recurring charges",
                onClick = onManageRecurring
            )
            ActionItem(
                icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MatrixGreen) },
                title = "Edit Name",
                subtitle = "> Rename this entry",
                onClick = onEditName
            )
            ActionItem(
                icon = { Text("X", style = MaterialTheme.typography.titleLarge, color = MatrixRed) },
                title = "Terminate",
                subtitle = "> Delete entry and all records",
                onClick = onDelete,
                isDanger = true
            )
        }
    }
}

@Composable
fun ActionItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDanger: Boolean = false
) {
    val borderColor = if (isDanger) MatrixRed else MatrixGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = if (isDanger) MatrixRed else MatrixGreen
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MatrixGreenDim
            )
        }
    }
}

@Composable
fun MatrixDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, MatrixGreen, RoundedCornerShape(4.dp))
                .background(MatrixDark)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MatrixGreen,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
                    .background(MatrixDark)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "// ${title.uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MatrixGreen
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MatrixGreen
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun MatrixChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) MatrixGreen.copy(alpha = 0.2f) else MatrixBlack
    val borderColor = if (selected) MatrixGreen else MatrixGreenBorder

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MatrixGreen else MatrixGreenDim
        )
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDanger: Boolean = false
) {
    MatrixDialog(
        title = title,
        onDismiss = onDismiss
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MatrixGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            MatrixButton(
                text = "Cancel",
                onClick = onDismiss
            )
            MatrixButton(
                text = "Confirm",
                onClick = onConfirm,
                isDanger = isDanger
            )
        }
    }
}
