package com.calculator.vault.privacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.R
import com.calculator.vault.privacy.domain.model.InstalledApp
import com.calculator.vault.privacy.domain.model.VaultApp
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.PremiumEmptyState
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
    onCloneInstalled: (InstalledApp) -> Unit,
    onEnableCloneSpace: () -> Unit,
    onOpenSamsungDualMessenger: () -> Unit,
    onDismissMessage: () -> Unit,
    onPickerQueryChange: (String) -> Unit,
) {
    if (uiState.showPicker) {
        AppPickerDialog(
            apps = uiState.installedApps,
            query = uiState.pickerQuery,
            cloneSpaceReady = uiState.cloneSpaceReady,
            onQueryChange = onPickerQueryChange,
            onDismiss = { onShowPicker(false) },
            onShortcut = onLaunchInstalled,
            onClone = onCloneInstalled,
        )
    }
    uiState.userMessage?.let { message ->
        AlertDialog(
            onDismissRequest = onDismissMessage,
            title = { Text(stringResource(R.string.clone_space_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismissMessage) { Text("OK") }
            },
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
            subtitle = "Protected shortcuts and cloned apps",
            modifier = Modifier.padding(padding),
        ) {
            if (!uiState.cloneSpaceReady) {
                VaultCard(
                    title = stringResource(R.string.clone_space_title),
                    description = uiState.cloneSpaceMessage,
                    icon = Icons.Outlined.Apps,
                    onClick = { if (uiState.cloneSpaceCanEnable) onEnableCloneSpace() },
                )
                if (uiState.cloneSpaceCanEnable) {
                    OutlinedButton(onClick = onEnableCloneSpace, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.clone_space_enable))
                    }
                } else if (uiState.cloneSpaceShowSamsungDual) {
                    OutlinedButton(
                        onClick = onOpenSamsungDualMessenger,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.clone_space_open_dual_messenger))
                    }
                }
            } else {
                Text(uiState.cloneSpaceMessage)
            }
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search vault apps") },
                singleLine = true,
            )
            if (uiState.loading) {
                CircularProgressIndicator()
                return@OneUiScaffold
            }
            if (uiState.vaultApps.isEmpty()) {
                PremiumEmptyState(
                    icon = Icons.Outlined.Apps,
                    title = "No Apps Yet",
                    description = "Add a shortcut or clone an app to keep a private copy with separate data.",
                    actionLabel = "Add App",
                    onAction = { onShowPicker(true) },
                )
                return@OneUiScaffold
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.vaultApps.forEach { app ->
                    val badge = buildString {
                        if (app.isFavorite) append("★ ")
                        if (app.isClone) append(stringResource(R.string.clone_badge))
                    }.trim().ifBlank { null }
                    VaultCard(
                        title = app.label,
                        description = buildString {
                            append(app.category)
                            append(" • ")
                            append(app.launchCount)
                            append(" launches")
                            if (app.isClone) append(" • separate data")
                        },
                        icon = Icons.Outlined.Apps,
                        status = badge,
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
    query: String,
    cloneSpaceReady: Boolean,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onShortcut: (InstalledApp) -> Unit,
    onClone: (InstalledApp) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose an app") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.picker_search_apps)) },
                    singleLine = true,
                )
                LazyColumn(
                    modifier = Modifier.heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (apps.isEmpty()) {
                        item { Text("No matching apps found.") }
                    } else {
                        items(apps, key = { it.packageName }) { app ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(app.label)
                                Text(
                                    app.packageName,
                                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = { onShortcut(app) },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(stringResource(R.string.shortcut_app))
                                    }
                                    Button(
                                        onClick = { onClone(app) },
                                        modifier = Modifier.weight(1f),
                                        enabled = cloneSpaceReady,
                                    ) {
                                        Text(stringResource(R.string.clone_app))
                                    }
                                }
                            }
                        }
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
