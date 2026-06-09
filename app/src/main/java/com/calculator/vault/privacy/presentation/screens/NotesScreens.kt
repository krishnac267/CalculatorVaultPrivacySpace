package com.calculator.vault.privacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.calculator.vault.privacy.presentation.testing.TestTags
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.components.PremiumEmptyState
import com.calculator.vault.privacy.presentation.components.VaultCard
import com.calculator.vault.privacy.presentation.viewmodels.NoteEditorUiState
import com.calculator.vault.privacy.presentation.viewmodels.NotesUiState

@Composable
fun NotesScreen(
    uiState: NotesUiState,
    onQueryChange: (String) -> Unit,
    onCreateNote: () -> Unit,
    onOpenNote: (Long) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNote,
                modifier = Modifier.testTag(TestTags.NOTES_CREATE),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Create Note")
            }
        },
    ) { padding ->
        OneUiScaffold(
            title = "Notes",
            subtitle = "Your secure notes",
            modifier = Modifier.padding(padding),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().testTag(TestTags.NOTES_SEARCH),
                label = { Text("Search notes") },
                singleLine = true,
            )
            if (uiState.loading) {
                CircularProgressIndicator()
                return@OneUiScaffold
            }
        if (uiState.notes.isEmpty()) {
            PremiumEmptyState(
                icon = Icons.Outlined.Description,
                title = "No Notes Yet",
                description = "Create your first secure note. Everything stays in your private vault.",
                actionLabel = "Create Note",
                onAction = onCreateNote,
            )
            return@OneUiScaffold
        }
            uiState.notes.forEach { note ->
                val status = buildString {
                    if (note.isFavorite) append("★ ")
                    if (note.isLocked) append("🔒")
                }.trim().ifBlank { null }
                VaultCard(
                    title = note.title,
                    description = when {
                        note.isLocked -> "Locked note"
                        note.isContentHidden -> "Encrypted note"
                        else -> note.content.take(80)
                    },
                    icon = Icons.Outlined.Description,
                    status = status,
                    onClick = { onOpenNote(note.id) },
                )
            }
        }
    }
}

@Composable
fun NoteEditorScreen(
    uiState: NoteEditorUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleLocked: () -> Unit,
    onUnlockPinChange: (String) -> Unit,
    onUnlockWithPin: () -> Unit,
    onBack: () -> Unit,
) {
    if (uiState.needsPinUnlock) {
        AlertDialog(
            onDismissRequest = onBack,
            modifier = Modifier.testTag(TestTags.PIN_UNLOCK_DIALOG),
            title = { Text("Unlock note") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your vault PIN to view this locked note.")
                    OutlinedTextField(
                        value = uiState.pinInput,
                        onValueChange = onUnlockPinChange,
                        modifier = Modifier.testTag(TestTags.PIN_UNLOCK_INPUT),
                        label = { Text("PIN") },
                        singleLine = true,
                    )
                    uiState.pinError?.let { Text(it) }
                }
            },
            confirmButton = {
                Button(onClick = onUnlockWithPin) { Text("Unlock") }
            },
            dismissButton = {
                Button(onClick = onBack) { Text("Back") }
            },
        )
    }
    OneUiScaffold(
        title = if (uiState.isNew) "New Note" else "Edit Note",
        subtitle = "Private and encrypted locally",
    ) {
        if (uiState.loading) {
            CircularProgressIndicator()
            return@OneUiScaffold
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onToggleFavorite) {
                Icon(Icons.Outlined.Star, contentDescription = "Favorite")
            }
            IconButton(onClick = onToggleLocked, modifier = Modifier.testTag(TestTags.NOTE_LOCK)) {
                Icon(Icons.Outlined.Lock, contentDescription = "Lock note")
            }
        }
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.NOTE_TITLE),
            label = { Text("Title") },
            singleLine = true,
        )
        OutlinedTextField(
            value = uiState.content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.NOTE_CONTENT),
            label = { Text("Content") },
            minLines = 8,
            enabled = !uiState.needsPinUnlock,
        )
        uiState.error?.let { Text(it) }
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.NOTE_SAVE),
            enabled = !uiState.saving,
        ) {
            Text(if (uiState.saving) "Saving..." else "Save Note")
        }
        if (!uiState.isNew) {
            Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Delete, contentDescription = null)
                Text("Delete Note")
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
