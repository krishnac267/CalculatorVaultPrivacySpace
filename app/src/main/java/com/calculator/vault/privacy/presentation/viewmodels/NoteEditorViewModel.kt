package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.model.LockedNoteException
import com.calculator.vault.privacy.domain.model.SecureNote
import com.calculator.vault.privacy.domain.usecases.CreateNoteUseCase
import com.calculator.vault.privacy.domain.usecases.DeleteNoteUseCase
import com.calculator.vault.privacy.domain.usecases.GetNoteUseCase
import com.calculator.vault.privacy.domain.usecases.ToggleNoteFavoriteUseCase
import com.calculator.vault.privacy.domain.usecases.ToggleNoteLockedUseCase
import com.calculator.vault.privacy.domain.usecases.UnlockNoteUseCase
import com.calculator.vault.privacy.domain.usecases.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteEditorUiState(
    val noteId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val favorite: Boolean = false,
    val locked: Boolean = false,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val needsPinUnlock: Boolean = false,
    val pinInput: String = "",
    val pinError: String? = null,
    val error: String? = null,
    val isNew: Boolean = true,
)

sealed interface NoteEditorEvent {
    data object Saved : NoteEditorEvent
    data object Deleted : NoteEditorEvent
}

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteUseCase: GetNoteUseCase,
    private val unlockNoteUseCase: UnlockNoteUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val toggleNoteFavoriteUseCase: ToggleNoteFavoriteUseCase,
    private val toggleNoteLockedUseCase: ToggleNoteLockedUseCase,
) : ViewModel() {

    private val routeNoteId: Long = savedStateHandle.get<String>("noteId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow(NoteEditorUiState(isNew = routeNoteId <= 0L))
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NoteEditorEvent>()
    val events = _events.asSharedFlow()

    init {
        if (routeNoteId > 0L) {
            loadNote(routeNoteId)
        } else {
            _uiState.update { it.copy(loading = false) }
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            try {
                val note: SecureNote = getNoteUseCase.execute(id)
                if (note.isLocked && note.isContentHidden) {
                    _uiState.update {
                        it.copy(
                            noteId = note.id,
                            title = note.title,
                            favorite = note.isFavorite,
                            locked = true,
                            loading = false,
                            needsPinUnlock = true,
                            isNew = false,
                        )
                    }
                } else {
                    applyNote(note)
                }
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(loading = false, error = "Note not found") }
            }
        }
    }

    private fun applyNote(note: SecureNote) {
        _uiState.update {
            it.copy(
                noteId = note.id,
                title = note.title,
                content = note.content,
                favorite = note.isFavorite,
                locked = note.isLocked,
                loading = false,
                needsPinUnlock = false,
                pinInput = "",
                pinError = null,
                isNew = false,
            )
        }
    }

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value, error = null) }
    }

    fun updateContent(value: String) {
        _uiState.update { it.copy(content = value, error = null) }
    }

    fun updatePinInput(value: String) {
        _uiState.update { it.copy(pinInput = value.filter { ch -> ch.isDigit() }.take(12), pinError = null) }
    }

    fun unlockWithPin() {
        val state = _uiState.value
        if (state.noteId <= 0L) return
        viewModelScope.launch {
            try {
                val note = unlockNoteUseCase.execute(state.noteId, state.pinInput)
                applyNote(note)
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(pinError = "Incorrect PIN") }
            }
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.needsPinUnlock) return
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true) }
            try {
                if (state.isNew) {
                    createNoteUseCase.execute(state.title, state.content, state.locked)
                } else {
                    updateNoteUseCase.execute(state.noteId, state.title, state.content)
                }
                _uiState.update { it.copy(saving = false) }
                _events.emit(NoteEditorEvent.Saved)
            } catch (e: LockedNoteException) {
                _uiState.update { it.copy(saving = false, needsPinUnlock = true, pinError = "Unlock note to save") }
            }
        }
    }

    fun delete() {
        val id = _uiState.value.noteId
        if (id <= 0L) return
        viewModelScope.launch {
            deleteNoteUseCase.execute(id)
            _events.emit(NoteEditorEvent.Deleted)
        }
    }

    fun toggleFavorite() {
        val id = _uiState.value.noteId
        if (id <= 0L) {
            _uiState.update { it.copy(favorite = !it.favorite) }
            return
        }
        viewModelScope.launch {
            toggleNoteFavoriteUseCase.execute(id)
            _uiState.update { it.copy(favorite = !it.favorite) }
        }
    }

    fun toggleLocked() {
        val id = _uiState.value.noteId
        if (id <= 0L) {
            _uiState.update { it.copy(locked = !it.locked) }
            return
        }
        viewModelScope.launch {
            toggleNoteLockedUseCase.execute(id)
            _uiState.update { it.copy(locked = !it.locked, needsPinUnlock = !it.locked) }
        }
    }
}
