package com.calculator.vault.privacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.calculator.vault.privacy.domain.model.IntruderLog
import com.calculator.vault.privacy.presentation.components.AnimatedProgressBar
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.VaultCard
import com.calculator.vault.privacy.presentation.viewmodels.SecurityCenterUiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityCenterScreen(
    uiState: SecurityCenterUiState,
    onBiometricChange: (Boolean) -> Unit,
    onIntruderCaptureChange: (Boolean) -> Unit,
    onTimeoutChange: (Int) -> Unit,
    onDeleteLog: (Long) -> Unit,
    onPanicLogout: () -> Unit,
) {
    var showPanicDialog by remember { mutableStateOf(false) }
    if (showPanicDialog) {
        AlertDialog(
            onDismissRequest = { showPanicDialog = false },
            title = { Text("Panic Logout") },
            text = { Text("This wipes all vault credentials and returns to the calculator. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    showPanicDialog = false
                    onPanicLogout()
                }) { Text("Wipe & Lock") }
            },
            dismissButton = { TextButton(onClick = { showPanicDialog = false }) { Text("Cancel") } },
        )
    }

    OneUiScaffold(title = "Security Center", subtitle = "Monitor and control your vault posture") {
        if (uiState.loading && uiState.analytics == null) {
            CircularProgressIndicator()
            return@OneUiScaffold
        }
        val analytics = uiState.analytics ?: return@OneUiScaffold

        AnimatedProgressBar(
            progress = analytics.securityScore,
            label = "Security score ${analytics.securityScore}/100",
        )

        if (analytics.isRooted || analytics.isEmulator) {
            VaultCard(
                title = "Device Warning",
                description = buildString {
                    if (analytics.isRooted) append("Root detected. ")
                    if (analytics.isEmulator) append("Emulator detected.")
                }.trim(),
                icon = Icons.Outlined.Warning,
                status = "Alert",
            )
        }

        Text("Protection", style = MaterialTheme.typography.headlineMedium)
        ToggleRow("Biometric unlock", analytics.isBiometricEnabled, onBiometricChange)
        ToggleRow("Intruder selfie capture", analytics.isIntruderCaptureEnabled, onIntruderCaptureChange)
        VaultCard(
            title = "Session timeout",
            description = "Auto-lock after ${analytics.sessionTimeoutMinutes} minutes",
            icon = Icons.Outlined.Shield,
            onClick = {
                val next = when (analytics.sessionTimeoutMinutes) {
                    1 -> 5
                    5 -> 15
                    15 -> 30
                    else -> 1
                }
                onTimeoutChange(next)
            },
        )
        VaultCard(
            title = "Panic logout",
            description = "Instantly wipe credentials and lock",
            icon = Icons.Outlined.Shield,
            onClick = { showPanicDialog = true },
        )

        Text("Intruder Gallery", style = MaterialTheme.typography.headlineMedium)
        if (uiState.intruderLogs.isEmpty()) {
            Text("No intruder events recorded.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.intruderLogs, key = { it.id }) { log ->
                    IntruderLogCard(log, onDelete = { onDeleteLog(log.id) })
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun IntruderLogCard(log: IntruderLog, onDelete: () -> Unit) {
    VaultCard(
        title = "Failed PIN • ${log.attemptCount} attempts",
        description = log.detail,
        icon = Icons.Outlined.Shield,
        status = formatTime(log.timestamp),
    )
    if (log.hasPhoto()) {
        AsyncImage(
            model = File(log.photoPath),
            contentDescription = "Intruder capture",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
    }
    IconButton(onClick = onDelete) {
        Icon(Icons.Outlined.Delete, contentDescription = "Delete intruder log")
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(timestamp))
