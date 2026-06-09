package com.calculator.vault.privacy.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.calculator.vault.privacy.domain.model.VaultFile
import com.calculator.vault.privacy.domain.model.VaultFileCategory
import com.calculator.vault.privacy.presentation.components.AnimatedProgressBar
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.PremiumEmptyState
import com.calculator.vault.privacy.presentation.components.VaultCard
import com.calculator.vault.privacy.presentation.viewmodels.FilesTab
import com.calculator.vault.privacy.presentation.viewmodels.FilesUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FilesScreen(
    uiState: FilesUiState,
    onTabSelected: (FilesTab) -> Unit,
    onQueryChange: (String) -> Unit,
    onImport: (Uri, String?, String?) -> Unit,
    onDelete: (Long) -> Unit,
    onRestore: (Long) -> Unit,
    onPermanentDelete: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onToggleDeletedView: () -> Unit,
    onPreview: (Long) -> Unit,
) {
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            onImport(uri, null, null)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!uiState.showDeleted) {
                ExtendedFloatingActionButton(
                    onClick = {
                        importLauncher.launch(
                            arrayOf(
                                "image/*",
                                "video/*",
                                "application/pdf",
                                "application/*",
                                "text/*",
                            ),
                        )
                    },
                ) {
                    Icon(Icons.Outlined.Upload, contentDescription = null)
                    Text(if (uiState.importing) "Importing..." else "Import")
                }
            }
        },
    ) { padding ->
        OneUiScaffold(
            title = "File Vault",
            subtitle = "Encrypted copies in app-private storage",
            modifier = Modifier.padding(padding),
        ) {
            StorageAnalyticsSection(uiState)

            TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                FilesTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        text = { Text(tab.label()) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onToggleDeletedView) {
                    Text(if (uiState.showDeleted) "Show active files" else "Show deleted")
                }
            }

            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search files") },
                singleLine = true,
            )

            uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (uiState.loading) {
                CircularProgressIndicator()
                return@OneUiScaffold
            }

            if (uiState.files.isEmpty()) {
                PremiumEmptyState(
                    icon = Icons.Outlined.Folder,
                    title = if (uiState.showDeleted) "Trash is empty" else "No Files Yet",
                    description = if (uiState.showDeleted) {
                        "Deleted files appear here until permanently removed."
                    } else {
                        "Import images, videos, PDFs, and documents via the system file picker."
                    },
                    actionLabel = if (uiState.showDeleted) null else "Import File",
                    onAction = if (uiState.showDeleted) null else {
                        {
                            importLauncher.launch(
                                arrayOf("image/*", "video/*", "application/pdf", "application/*", "text/*"),
                            )
                        }
                    },
                )
                return@OneUiScaffold
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.files, key = { it.id }) { file ->
                    FileVaultCard(
                        file = file,
                        showDeleted = uiState.showDeleted,
                        onPreview = onPreview,
                        onDelete = onDelete,
                        onRestore = onRestore,
                        onPermanentDelete = onPermanentDelete,
                        onToggleFavorite = onToggleFavorite,
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageAnalyticsSection(uiState: FilesUiState) {
    AnimatedVisibility(
        visible = uiState.totalBytes > 0L,
        enter = fadeIn(tween(400)) + slideInVertically { it / 4 },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimatedProgressBar(
                progress = usagePercent(uiState.totalBytes),
                label = "Vault storage • ${formatBytes(uiState.totalBytes)}",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VaultCard(
                    title = "Images",
                    description = formatBytes(uiState.imageBytes),
                    icon = Icons.Outlined.Image,
                    modifier = Modifier.weight(1f),
                )
                VaultCard(
                    title = "Videos",
                    description = formatBytes(uiState.videoBytes),
                    icon = Icons.Outlined.VideoLibrary,
                    modifier = Modifier.weight(1f),
                )
            }
            VaultCard(
                title = "Documents",
                description = formatBytes(uiState.documentBytes),
                icon = Icons.Outlined.Description,
            )
        }
    }
}

@Composable
private fun FileVaultCard(
    file: VaultFile,
    showDeleted: Boolean,
    onPreview: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onRestore: (Long) -> Unit,
    onPermanentDelete: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
) {
    val icon = when (file.category) {
        VaultFileCategory.IMAGE -> Icons.Outlined.Image
        VaultFileCategory.VIDEO -> Icons.Outlined.VideoLibrary
        VaultFileCategory.PDF, VaultFileCategory.DOCUMENT -> Icons.Outlined.Description
    }
    val status = buildString {
        if (file.isFavorite) append("★ ")
        append(formatBytes(file.sizeBytes))
    }.trim()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VaultCard(
            title = file.displayName,
            description = "${file.category.name.lowercase().replaceFirstChar { it.titlecase() }} • ${formatDate(file.importedAt)}",
            icon = icon,
            status = status,
            onClick = { if (!showDeleted) onPreview(file.id) },
            modifier = Modifier.weight(1f),
        )
        if (showDeleted) {
            IconButton(onClick = { onRestore(file.id) }) {
                Icon(Icons.Outlined.Restore, contentDescription = "Restore")
            }
            IconButton(onClick = { onPermanentDelete(file.id) }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete permanently")
            }
        } else {
            IconButton(onClick = { onToggleFavorite(file.id) }) {
                Icon(Icons.Outlined.Star, contentDescription = "Favorite")
            }
            IconButton(onClick = { onDelete(file.id) }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun FilePreviewDialog(
    fileName: String,
    previewFile: java.io.File?,
    mimeType: String?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(fileName) },
        text = {
            if (previewFile == null) {
                Text("Unable to load preview.")
            } else {
                Text("Open decrypted preview in a secure viewer.")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (previewFile != null) {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            previewFile,
                        )
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, mimeType ?: "*/*")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Preview"))
                    }
                    onDismiss()
                },
                enabled = previewFile != null,
            ) { Text("Open") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

private fun FilesTab.label(): String = when (this) {
    FilesTab.IMAGES -> "Images"
    FilesTab.VIDEOS -> "Videos"
    FilesTab.DOCUMENTS -> "Documents"
    FilesTab.RECENT -> "Recent"
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> String.format(Locale.US, "%.1f GB", bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> String.format(Locale.US, "%.1f MB", bytes / 1_048_576.0)
    bytes >= 1024L -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
    else -> "$bytes B"
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))

private fun usagePercent(totalBytes: Long): Int {
    val limit = 500L * 1024L * 1024L
    return ((totalBytes * 100) / limit).toInt().coerceIn(0, 100)
}
