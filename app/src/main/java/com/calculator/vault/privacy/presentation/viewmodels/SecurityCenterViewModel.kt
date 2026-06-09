package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.interfaces.IntruderRepository
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.model.IntruderLog
import com.calculator.vault.privacy.domain.model.SecurityAnalytics
import com.calculator.vault.privacy.domain.usecases.LoadSecurityAnalyticsUseCase
import com.calculator.vault.privacy.domain.usecases.PanicLogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecurityCenterUiState(
    val loading: Boolean = true,
    val analytics: SecurityAnalytics? = null,
    val intruderLogs: List<IntruderLog> = emptyList(),
)

sealed interface SecurityCenterEvent {
    data object PanicCompleted : SecurityCenterEvent
}

@HiltViewModel
class SecurityCenterViewModel @Inject constructor(
    private val loadSecurityAnalyticsUseCase: LoadSecurityAnalyticsUseCase,
    private val intruderRepository: IntruderRepository,
    private val securityRepository: SecurityRepository,
    private val panicLogoutUseCase: PanicLogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityCenterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SecurityCenterEvent>()
    val events = _events.asSharedFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = false,
                    analytics = loadSecurityAnalyticsUseCase.execute(),
                    intruderLogs = intruderRepository.getRecentLogs(20),
                )
            }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        securityRepository.setBiometricEnabled(enabled)
        refresh()
    }

    fun setIntruderCaptureEnabled(enabled: Boolean) {
        securityRepository.setIntruderCaptureEnabled(enabled)
        refresh()
    }

    fun setSessionTimeout(minutes: Int) {
        securityRepository.setSessionTimeoutMinutes(minutes)
        refresh()
    }

    fun deleteIntruderLog(id: Long) {
        intruderRepository.deleteLog(id)
        refresh()
    }

    fun panicLogout() {
        viewModelScope.launch {
            panicLogoutUseCase.execute()
            _events.emit(SecurityCenterEvent.PanicCompleted)
        }
    }
}
