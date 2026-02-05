package com.debttracker.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.debttracker.app.ui.DebtTrackerViewModel
import com.debttracker.app.ui.components.*
import com.debttracker.app.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    viewModel: DebtTrackerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showClearDataDialog by remember { mutableStateOf(false) }
    var walletCopiedMessage by remember { mutableStateOf<String?>(null) }

    // File picker for export
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = viewModel.getBackupJsonAsync()
                    context.contentResolver.openOutputStream(it)?.use { output ->
                        output.write(json.toByteArray())
                    }
                    Toast.makeText(context, "> Export complete", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "ERROR: Export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val json = context.contentResolver.openInputStream(it)?.bufferedReader()?.readText()
                if (json != null) {
                    viewModel.restoreFromJson(json)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "ERROR: Import failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        MatrixHeader(
            title = "System Config",
            leadingContent = {
                MatrixIconButton(
                    icon = { Icon(Icons.Default.ArrowBack, contentDescription = "Back") },
                    onClick = onBack
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Backup Section
            SettingsSection(title = "Data Backup") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MatrixButton(
                        text = "EXPORT",
                        onClick = {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val filename = "debt-tracker-backup-${dateFormat.format(Date())}.json"
                            exportLauncher.launch(filename)
                        },
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                    MatrixButton(
                        text = "SHARE",
                        onClick = {
                            scope.launch {
                                try {
                                    val json = viewModel.getBackupJsonAsync()
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val filename = "debt-tracker-backup-${dateFormat.format(Date())}.json"

                                    // Create backups directory in cache
                                    val backupsDir = File(context.cacheDir, "backups")
                                    backupsDir.mkdirs()

                                    // Write backup file
                                    val backupFile = File(backupsDir, filename)
                                    backupFile.writeText(json)

                                    // Get shareable URI via FileProvider
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        backupFile
                                    )

                                    // Share the file
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_SUBJECT, "Debt Tracker Backup")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share backup"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "ERROR: Share failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                    MatrixButton(
                        text = "IMPORT",
                        onClick = {
                            importLauncher.launch(arrayOf("application/json"))
                        },
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "> Export saves locally. Share transmits via external protocols.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixGreenDim
                )
            }

            // Danger Zone
            SettingsSection(title = "Danger Zone") {
                MatrixButton(
                    text = "Purge All Data",
                    onClick = { showClearDataDialog = true },
                    isDanger = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "> WARNING: This action is irreversible.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixRed.copy(alpha = 0.7f)
                )
            }

            // Donate Section
            SettingsSection(title = "Support Development") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DonateButton(
                        label = "Bitcoin",
                        symbol = "₿",
                        color = BitcoinOrange,
                        address = "bc1qu88v7lvcf4kwq2mdsztlwsy4gl4jqc3whgvp6c",
                        context = context,
                        onCopied = { walletCopiedMessage = "BTC" },
                        modifier = Modifier.weight(1f)
                    )
                    DonateButton(
                        label = "Dogecoin",
                        symbol = "Ð",
                        color = DogecoinGold,
                        address = "DSBAYVv9KHXzrsyPVHAiajNEV7E95Xc8Zn",
                        context = context,
                        onCopied = { walletCopiedMessage = "DOGE" },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = walletCopiedMessage?.let { "> $it address copied!" }
                        ?: "> Tap to copy wallet address",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (walletCopiedMessage != null) MatrixGreen else MatrixGreenDim
                )
            }
        }
    }

    // Clear data confirmation
    if (showClearDataDialog) {
        ConfirmDialog(
            title = "FINAL WARNING",
            message = "All records will be permanently erased. This cannot be undone.",
            onConfirm = {
                viewModel.clearAllData()
                showClearDataDialog = false
            },
            onDismiss = { showClearDataDialog = false },
            isDanger = true
        )
    }

    // Reset wallet copied message after delay
    LaunchedEffect(walletCopiedMessage) {
        if (walletCopiedMessage != null) {
            kotlinx.coroutines.delay(2000)
            walletCopiedMessage = null
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    MatrixCard {
        Text(
            text = "> $title",
            style = MaterialTheme.typography.titleSmall,
            color = MatrixGreen
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun DonateButton(
    label: String,
    symbol: String,
    color: androidx.compose.ui.graphics.Color,
    address: String,
    context: Context,
    onCopied: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, address)
            clipboard.setPrimaryClip(clip)
            onCopied()
        },
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(text = "$symbol $label", style = MaterialTheme.typography.labelMedium)
    }
}
