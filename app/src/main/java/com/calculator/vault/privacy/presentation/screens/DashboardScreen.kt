package com.calculator.vault.privacy.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.domain.model.SessionState
import com.calculator.vault.privacy.presentation.components.AnimatedProgressBar
import com.calculator.vault.privacy.presentation.components.DashboardSkeleton
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.VaultCard
import com.calculator.vault.privacy.presentation.viewmodels.DashboardUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onNavigateApps: () -> Unit,
    onNavigateNotes: () -> Unit,
    onNavigateFiles: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSecurityCenter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        OneUiScaffold(
            title = greeting(),
            subtitle = vaultSubtitle(uiState.summary?.sessionState),
        ) {
            if (uiState.loading && uiState.summary == null) {
                DashboardSkeleton()
                return@OneUiScaffold
            }
            val summary = uiState.summary
            val security = uiState.securityAnalytics
            val storage = uiState.storageAnalytics

            AnimatedVisibility(
                visible = security != null,
                enter = fadeIn(tween(400)) + slideInVertically { it / 4 },
            ) {
                security?.let {
                    VaultCard(
                        title = "Security Center",
                        description = "Posture score ${it.securityScore}/100 • ${it.intruderEventCount} intruder events",
                        icon = Icons.Outlined.Shield,
                        status = scoreLabel(it.securityScore),
                        onClick = onNavigateSecurityCenter,
                    )
                }
            }

            AnimatedVisibility(
                visible = storage != null,
                enter = fadeIn(tween(500)) + slideInVertically { it / 4 },
            ) {
                storage?.let {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val hasQuota = it.noteLimit < Int.MAX_VALUE / 2
                        if (hasQuota) {
                            AnimatedProgressBar(
                                progress = it.usagePercent,
                                label = "Storage • ${formatBytes(it.usedBytes)} of ${formatBytes(it.limitBytes)}",
                            )
                            Text(
                                text = "${it.noteCount}/${it.noteLimit} notes • ${it.appCount}/${it.appLimit} apps • ${it.notificationCount} alerts",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            Text(
                                text = "Storage • ${formatBytes(it.usedBytes)} used",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "${it.noteCount} notes • ${it.appCount} apps • ${it.notificationCount} alerts",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }

            Text("Quick Actions", style = MaterialTheme.typography.headlineMedium)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VaultCard("Apps", "${summary?.appCount ?: 0} protected", Icons.Outlined.Apps, onClick = onNavigateApps)
                VaultCard("Notes", "${summary?.noteCount ?: 0} secure notes", Icons.Outlined.Description, onClick = onNavigateNotes)
                VaultCard("Files", "${summary?.fileCount ?: 0} vault files", Icons.Outlined.Folder, onClick = onNavigateFiles)
                VaultCard(
                    "Notifications",
                    "${summary?.notificationCount ?: 0} stored alerts",
                    Icons.Outlined.Notifications,
                    onClick = onNavigateNotifications,
                )
            }

            Text("Recent Activity", style = MaterialTheme.typography.headlineMedium)
            if (summary?.recentApps.isNullOrEmpty()) {
                Text("Launch a protected app to see activity here.")
            } else {
                summary?.recentApps?.forEach { app ->
                    VaultCard(
                        title = app.label,
                        description = app.packageName,
                        icon = Icons.Outlined.Apps,
                        status = "${app.launchCount}x",
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Last login: ${formatTime(summary?.lastLoginAt ?: 0L)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun greeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

private fun vaultSubtitle(state: SessionState?): String = when (state) {
    SessionState.FAKE_VAULT -> "Decoy Vault Active"
    SessionState.REAL_VAULT -> "Your Private Space"
    else -> "Your Private Space"
}

private fun scoreLabel(score: Int): String = when {
    score >= 85 -> "Strong"
    score >= 60 -> "Good"
    else -> "Review"
}

private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0L) return "Not yet"
    return SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
    return String.format(Locale.US, "%.1f GB", mb / 1024.0)
}
