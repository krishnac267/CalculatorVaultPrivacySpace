package com.calculator.vault.privacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Star
import com.calculator.vault.privacy.presentation.components.PremiumEmptyState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.domain.model.InstalledApp
import com.calculator.vault.privacy.domain.model.VaultApp
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.VaultCard
import com.calculator.vault.privacy.presentation.viewmodels.AppsUiState

@Composable
fun AppsScreen(
    uiState: AppsUiState,
    onQueryChange: (String) -> Unit,
    onLaunchApp: (VaultApp) -> Unit,
    onToggleFavorite: (VaultApp) -> Unit,
    onShowPicker: (Boolean) -> Unit,
    onLaunchInstalled: (InstalledApp) -> Unit,
) {
    if (uiState.showPicker) {
        AppPickerDialog(
            apps = uiState.installedApps,
            onDismiss = { onShowPicker(false) },
            onSelect = onLaunchInstalled,
        )
    }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { onShowPicker(true) }) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Text("Add App")
            }
        },
    ) { padding ->
        OneUiScaffold(
            title = "Apps",
            subtitle = "Protected shortcuts and favorites",
            modifier = Modifier.padding(padding),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search apps") },
                singleLine = true,
            )
            if (uiState.loading) {
                CircularProgressIndicator()
                return@OneUiScaffold
            }
            if (uiState.vaultApps.isEmpty()) {
                Text("No protected apps yet. Tap Add App to launch and track an app.")
                return@OneUiScaffold
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.vaultApps.forEach { app ->
                    VaultCard(
                        title = app.label,
                        description = "${app.category} • ${app.launchCount} launches",
                        icon = Icons.Outlined.Apps,
                        status = if (app.isFavorite) "★" else null,
                        onClick = { onLaunchApp(app) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppPickerDialog(
    apps: List<InstalledApp>,
    onDismiss: () -> Unit,
    onSelect: (InstalledApp) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose an app") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(apps.take(50)) { app ->
                    TextButton(onClick = { onSelect(app) }, modifier = Modifier.fillMaxWidth()) {
                        Text(app.label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
fun SettingsScreen(
    uiState: com.calculator.vault.privacy.presentation.viewmodels.SettingsUiState,
    onNavigateSecurityCenter: () -> Unit,
    onBiometricChange: (Boolean) -> Unit,
    onTimeoutChange: (Int) -> Unit,
    onPanicLogout: () -> Unit,
) {
    OneUiScaffold(title = "Settings", subtitle = "Security, privacy, and appearance") {
        VaultCard(
            title = "Security Center",
            description = "Score, intruder gallery, and protection controls",
            icon = Icons.Outlined.Shield,
            onClick = onNavigateSecurityCenter,
        )
        VaultCard(
            title = "Biometric Unlock",
            description = "Use fingerprint or face from the calculator",
            icon = Icons.Outlined.Apps,
            status = if (uiState.biometricEnabled) "On" else "Off",
            onClick = { onBiometricChange(!uiState.biometricEnabled) },
        )
        VaultCard(
            title = "Session Timeout",
            description = "Auto-lock after ${uiState.sessionTimeoutMinutes} minutes",
            icon = Icons.Outlined.Apps,
            onClick = {
                val next = when (uiState.sessionTimeoutMinutes) {
                    1 -> 5
                    5 -> 15
                    15 -> 30
                    else -> 1
                }
                onTimeoutChange(next)
            },
        )
    }
}
