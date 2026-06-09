package com.calculator.vault.privacy.presentation.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.core.security.IntruderCaptureCoordinator
import com.calculator.vault.privacy.core.utilities.CalculatorEngine
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.model.PinValidationResult
import com.calculator.vault.privacy.domain.usecases.HandleIntruderEventUseCase
import com.calculator.vault.privacy.domain.usecases.IsSetupCompleteUseCase
import com.calculator.vault.privacy.domain.usecases.SeedFakeVaultUseCase
import com.calculator.vault.privacy.domain.usecases.UnlockWithBiometricUseCase
import com.calculator.vault.privacy.domain.usecases.ValidatePinUseCase
import com.calculator.vault.privacy.presentation.components.BiometricAuthHelper
import com.calculator.vault.privacy.presentation.components.BiometricStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorUiState(
    val display: String = "0",
    val scientificMode: Boolean = false,
    val setupComplete: Boolean = false,
    val biometricEnabled: Boolean = false,
    val showBiometricButton: Boolean = false,
)

sealed interface CalculatorEvent {
    data object NavigateSetup : CalculatorEvent
    data object NavigateVault : CalculatorEvent
}

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculatorEngine: CalculatorEngine,
    private val validatePinUseCase: ValidatePinUseCase,
    private val seedFakeVaultUseCase: SeedFakeVaultUseCase,
    private val unlockWithBiometricUseCase: UnlockWithBiometricUseCase,
    private val isSetupCompleteUseCase: IsSetupCompleteUseCase,
    private val securityRepository: SecurityRepository,
    private val handleIntruderEventUseCase: HandleIntruderEventUseCase,
    private val intruderCaptureCoordinator: IntruderCaptureCoordinator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CalculatorEvent>()
    val events = _events.asSharedFlow()

    init {
        refreshSetupState()
    }

    fun refreshSetupState() {
        _uiState.update {
            it.copy(
                setupComplete = isSetupCompleteUseCase.execute(),
                biometricEnabled = securityRepository.isBiometricEnabled(),
            )
        }
    }

    fun onBiometricAvailabilityChecked(status: BiometricStatus) {
        _uiState.update {
            it.copy(showBiometricButton = it.biometricEnabled && status == BiometricStatus.AVAILABLE)
        }
    }

    fun requestBiometricUnlock(activity: FragmentActivity) {
        if (!_uiState.value.biometricEnabled || !_uiState.value.setupComplete) return
        BiometricAuthHelper.authenticate(
            activity = activity,
            onSuccess = {
                viewModelScope.launch {
                    if (unlockWithBiometricUseCase.execute()) {
                        _events.emit(CalculatorEvent.NavigateVault)
                    }
                }
            },
        )
    }

    fun onDigit(digit: String) = updateDisplay { calculatorEngine.inputDigit(digit) }
    fun onDecimal() = updateDisplay { calculatorEngine.inputDecimal() }
    fun onOperator(op: String) = updateDisplay { calculatorEngine.inputOperator(op) }
    fun onPercent() = updateDisplay { calculatorEngine.inputPercent() }
    fun onSquareRoot() = updateDisplay { calculatorEngine.inputSquareRoot() }
    fun onPower() = updateDisplay { calculatorEngine.inputPower() }
    fun onClear() = updateDisplay { calculatorEngine.clear() }
    fun onBackspace() = updateDisplay { calculatorEngine.backspace() }
    fun onToggleScientific() {
        val enabled = !_uiState.value.scientificMode
        calculatorEngine.setScientificMode(enabled)
        _uiState.update { it.copy(scientificMode = enabled) }
    }

    fun onEquals() {
        if (!_uiState.value.setupComplete) {
            viewModelScope.launch { _events.emit(CalculatorEvent.NavigateSetup) }
            return
        }
        if (calculatorEngine.isPinAttempt()) {
            val pin = calculatorEngine.getPinForValidation()
            viewModelScope.launch {
                when (val result = validatePinUseCase.execute(pin)) {
                    PinValidationResult.INVALID -> {
                        calculatorEngine.evaluate()
                        _uiState.update { it.copy(display = calculatorEngine.getDisplay()) }
                        handleIntruderIfNeeded()
                    }
                    PinValidationResult.REAL_VAULT -> {
                        seedFakeVaultUseCase.execute(result)
                        _events.emit(CalculatorEvent.NavigateVault)
                    }
                    PinValidationResult.FAKE_VAULT -> {
                        seedFakeVaultUseCase.execute(result)
                        _events.emit(CalculatorEvent.NavigateVault)
                    }
                }
            }
            return
        }
        updateDisplay { calculatorEngine.evaluate() }
    }

    private suspend fun handleIntruderIfNeeded() {
        if (!handleIntruderEventUseCase.shouldLogIntruder()) return
        val photoPath = if (handleIntruderEventUseCase.isCaptureEnabled()) {
            intruderCaptureCoordinator.capturePhoto()
        } else {
            null
        }
        handleIntruderEventUseCase.logIntruder(photoPath)
    }

    private inline fun updateDisplay(block: () -> Unit) {
        block()
        _uiState.update { it.copy(display = calculatorEngine.getDisplay()) }
    }
}
