package com.calculator.vault.privacy.presentation.viewmodels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.core.clone.WorkProfileCloneManager
import com.calculator.vault.privacy.domain.model.CloneSpaceAlternative
import com.calculator.vault.privacy.domain.model.InstalledApp
import com.calculator.vault.privacy.domain.model.VaultApp
import com.calculator.vault.privacy.domain.usecases.BuildCloneSpaceSetupIntentUseCase
import com.calculator.vault.privacy.domain.usecases.CloneInstalledAppUseCase
import com.calculator.vault.privacy.domain.usecases.GetCloneSpaceStatusUseCase
import com.calculator.vault.privacy.domain.usecases.GetInstalledAppsUseCase
import com.calculator.vault.privacy.domain.usecases.GetVaultAppsUseCase
import com.calculator.vault.privacy.domain.usecases.LaunchAppUseCase
import com.calculator.vault.privacy.domain.usecases.ToggleAppFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsUiState(
    val loading: Boolean = true,
    val query: String = "",
    val pickerQuery: String = "",
    val vaultApps: List<VaultApp> = emptyList(),
    val installedApps: List<InstalledApp> = emptyList(),
    val showPicker: Boolean = false,
    val cloneSpaceReady: Boolean = false,
    val cloneSpaceCanEnable: Boolean = true,
    val cloneSpaceMessage: String = "",
    val cloneSpaceShowSamsungDual: Boolean = false,
    val cloneSpaceShowSecureFolder: Boolean = false,
    val userMessage: String? = null,
)

sealed interface AppsEvent {
    data class StartCloneSpaceSetup(val intent: Intent) : AppsEvent
    data class OpenExternalIntent(val intent: Intent) : AppsEvent
}

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getVaultAppsUseCase: GetVaultAppsUseCase,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val launchAppUseCase: LaunchAppUseCase,
    private val toggleAppFavoriteUseCase: ToggleAppFavoriteUseCase,
    private val getCloneSpaceStatusUseCase: GetCloneSpaceStatusUseCase,
    private val buildCloneSpaceSetupIntentUseCase: BuildCloneSpaceSetupIntentUseCase,
    private val cloneInstalledAppUseCase: CloneInstalledAppUseCase,
    private val workProfileCloneManager: WorkProfileCloneManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AppsEvent>()
    val events = _events.asSharedFlow()

    private var pendingCloneApp: InstalledApp? = null

    init {
        refresh()
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        refresh()
    }

    fun updatePickerQuery(query: String) {
        _uiState.update { it.copy(pickerQuery = query) }
        viewModelScope.launch {
            val installed = getInstalledAppsUseCase.execute(query)
            _uiState.update { it.copy(installedApps = installed) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            val query = _uiState.value.query
            val pickerQuery = _uiState.value.pickerQuery
            val cloneStatus = getCloneSpaceStatusUseCase.execute()
            _uiState.update {
                it.copy(
                    loading = false,
                    vaultApps = getVaultAppsUseCase.execute(query),
                    installedApps = getInstalledAppsUseCase.execute(pickerQuery),
                    cloneSpaceReady = cloneStatus.isReady,
                    cloneSpaceMessage = cloneStatus.message,
                    cloneSpaceCanEnable = cloneStatus.isSetupAvailable,
                    cloneSpaceShowSamsungDual =
                        cloneStatus.alternative == CloneSpaceAlternative.SAMSUNG_DUAL_MESSENGER,
                    cloneSpaceShowSecureFolder = workProfileCloneManager.isSamsungSecureFolderAvailable(),
                )
            }
        }
    }

    fun showPicker(show: Boolean) {
        if (show) {
            _uiState.update { it.copy(showPicker = true, pickerQuery = "", userMessage = null) }
            updatePickerQuery("")
        } else {
            _uiState.update { it.copy(showPicker = false) }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun openSamsungDualMessenger() {
        viewModelScope.launch {
            _events.emit(AppsEvent.OpenExternalIntent(workProfileCloneManager.buildSamsungDualMessengerIntent()))
        }
    }

    fun openSamsungSecureFolder() {
        viewModelScope.launch {
            _events.emit(AppsEvent.OpenExternalIntent(workProfileCloneManager.buildSamsungSecureFolderIntent()))
        }
    }

    fun enableCloneSpace() {
        viewModelScope.launch {
            if (workProfileCloneManager.shouldUseSamsungDualApps()) {
                openSamsungDualMessenger()
                _uiState.update {
                    it.copy(userMessage = "Samsung Knox blocks in-app setup. Use Dual Messenger or Secure Folder, then Add App → Clone.")
                }
                return@launch
            }
            val status = getCloneSpaceStatusUseCase.execute()
            if (!status.isSetupAvailable()) {
                _uiState.update { it.copy(userMessage = status.message) }
                return@launch
            }
            try {
                val intent = buildCloneSpaceSetupIntentUseCase.execute()
                _events.emit(AppsEvent.StartCloneSpaceSetup(intent))
            } catch (e: IllegalStateException) {
                _uiState.update { it.copy(userMessage = e.message ?: "Clone Space is not supported on this device") }
            }
        }
    }

    fun launchVaultApp(app: VaultApp) {
        viewModelScope.launch {
            try {
                launchAppUseCase.executeVaultApp(app)
                refresh()
            } catch (e: IllegalStateException) {
                _uiState.update { it.copy(userMessage = e.message) }
            }
        }
    }

    fun launchInstalledApp(app: InstalledApp) {
        viewModelScope.launch {
            launchAppUseCase.execute(app)
            _uiState.update { it.copy(showPicker = false) }
            refresh()
        }
    }

    fun cloneInstalledApp(activity: Activity, app: InstalledApp) {
        viewModelScope.launch {
            if (cloneInstalledAppUseCase.isAlreadyCloned(app.packageName)) {
                cloneInstalledAppUseCase.onInstallSucceeded(app)
                _uiState.update {
                    it.copy(
                        showPicker = false,
                        userMessage = "${app.label} is already cloned and added to your vault.",
                    )
                }
                refresh()
                return@launch
            }
            try {
                if (workProfileCloneManager.shouldUseSamsungDualApps()) {
                    pendingCloneApp = app
                    workProfileCloneManager.startCloneInstall(activity, app.packageName)
                    _uiState.update {
                        it.copy(
                            showPicker = false,
                            userMessage = "Turn on ${app.label} in Dual Messenger (or add it to Secure Folder), then return here — it will be added automatically.",
                        )
                    }
                    return@launch
                }
                val status = getCloneSpaceStatusUseCase.execute()
                if (!status.isReady) {
                    _uiState.update {
                        it.copy(userMessage = "${status.message} Tap Enable Clone Space first.")
                    }
                    return@launch
                }
                pendingCloneApp = app
                workProfileCloneManager.startCloneInstall(activity, app.packageName)
                _uiState.update {
                    it.copy(
                        showPicker = false,
                        userMessage = "Installing ${app.label} clone… Return here when Android finishes.",
                    )
                }
            } catch (e: IllegalStateException) {
                pendingCloneApp = null
                _uiState.update { it.copy(userMessage = e.message) }
            }
        }
    }

    fun checkPendingCloneInstall() {
        val app = pendingCloneApp ?: return
        viewModelScope.launch {
            if (!cloneInstalledAppUseCase.isAlreadyCloned(app.packageName)) {
                return@launch
            }
            cloneInstalledAppUseCase.onInstallSucceeded(app)
            pendingCloneApp = null
            _uiState.update { it.copy(userMessage = "${app.label} cloned successfully.") }
            refresh()
        }
    }

    fun toggleFavorite(app: VaultApp) {
        viewModelScope.launch {
            toggleAppFavoriteUseCase.execute(app.id)
            refresh()
        }
    }
}
