package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.model.SecureNote
import com.calculator.vault.privacy.domain.usecases.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val loading: Boolean = true,
    val query: String = "",
    val notes: List<SecureNote> = emptyList(),
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            val notes = getNotesUseCase.execute(_uiState.value.query)
            _uiState.update { it.copy(loading = false, notes = notes) }
        }
    }
}
