package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.interfaces.IntruderRepository
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.model.IntruderLog
import com.calculator.vault.privacy.domain.model.SessionState
import com.calculator.vault.privacy.domain.usecases.PanicLogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val biometricEnabled: Boolean = false,
    val sessionTimeoutMinutes: Int = 5,
    val failedAttempts: Int = 0,
    val intruderLogs: List<IntruderLog> = emptyList(),
    val sessionState: SessionState = SessionState.LOCKED,
)

sealed interface SettingsEvent {
    data object PanicCompleted : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val intruderRepository: IntruderRepository,
    private val panicLogoutUseCase: PanicLogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.update {
            SettingsUiState(
                biometricEnabled = securityRepository.isBiometricEnabled(),
                sessionTimeoutMinutes = securityRepository.getSessionTimeoutMinutes(),
                failedAttempts = securityRepository.getFailedPinAttempts(),
                intruderLogs = intruderRepository.getRecentLogs(5),
                sessionState = securityRepository.getSessionState(),
            )
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        securityRepository.setBiometricEnabled(enabled)
        _uiState.update { it.copy(biometricEnabled = enabled) }
    }

    fun setSessionTimeout(minutes: Int) {
        securityRepository.setSessionTimeoutMinutes(minutes)
        _uiState.update { it.copy(sessionTimeoutMinutes = minutes) }
    }

    fun panicLogout() {
        viewModelScope.launch {
            panicLogoutUseCase.execute()
            _events.emit(SettingsEvent.PanicCompleted)
        }
    }
}
