package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.usecases.IsSetupCompleteUseCase
import com.calculator.vault.privacy.domain.usecases.SetupVaultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val pin: String = "",
    val confirmPin: String = "",
    val fakePin: String = "",
    val biometricEnabled: Boolean = false,
    val error: String? = null,
    val step: Int = 0,
)

sealed interface SetupEvent {
    data object Completed : SetupEvent
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val setupVaultUseCase: SetupVaultUseCase,
    private val isSetupCompleteUseCase: IsSetupCompleteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SetupEvent>()
    val events = _events.asSharedFlow()

    init {
        if (isSetupCompleteUseCase.execute()) {
            viewModelScope.launch { _events.emit(SetupEvent.Completed) }
        }
    }

    fun updatePin(value: String) {
        if (value.length <= 8 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(pin = value, error = null) }
        }
    }

    fun updateConfirmPin(value: String) {
        if (value.length <= 8 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(confirmPin = value, error = null) }
        }
    }

    fun updateFakePin(value: String) {
        if (value.length <= 8 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(fakePin = value, error = null) }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _uiState.update { it.copy(biometricEnabled = enabled) }
    }

    fun nextStep() {
        val state = _uiState.value
        when (state.step) {
            0 -> {
                if (state.pin.length < 4) {
                    _uiState.update { it.copy(error = "PIN must be at least 4 digits") }
                } else {
                    _uiState.update { it.copy(step = 1, error = null) }
                }
            }
            1 -> {
                if (state.pin != state.confirmPin) {
                    _uiState.update { it.copy(error = "PINs do not match") }
                } else {
                    _uiState.update { it.copy(step = 2, error = null) }
                }
            }
            2 -> completeSetup()
        }
    }

    private fun completeSetup() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                setupVaultUseCase.execute(
                    state.pin,
                    state.fakePin.ifBlank { null },
                    state.biometricEnabled,
                )
                _events.emit(SetupEvent.Completed)
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
