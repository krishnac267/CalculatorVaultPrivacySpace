package com.calculator.vault.privacy.presentation.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.PremiumEmptyState
import com.calculator.vault.privacy.presentation.components.VaultCard
import com.calculator.vault.privacy.presentation.viewmodels.NotificationsUiState

@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    onQueryChange: (String) -> Unit,
) {
    val context = LocalContext.current
    OneUiScaffold(title = "Notifications", subtitle = "Private notification vault") {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search notifications") },
            singleLine = true,
        )
        if (uiState.loading) {
            CircularProgressIndicator()
            return@OneUiScaffold
        }
        if (uiState.notifications.isEmpty()) {
            PremiumEmptyState(
                icon = Icons.Outlined.Notifications,
                title = "No Notifications Yet",
                description = "Enable notification access to store alerts privately inside your vault.",
                actionLabel = "Open Notification Access",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                },
            )
            return@OneUiScaffold
        }
        Text("${uiState.unreadCount} unread")
        LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            items(uiState.notifications, key = { it.id }) { notification ->
                VaultCard(
                    title = notification.title.ifBlank { notification.appLabel },
                    description = "${notification.appLabel}: ${notification.body.take(100)}",
                    icon = Icons.Outlined.Notifications,
                    status = if (notification.isRead) null else "New",
                )
            }
        }
    }
}
