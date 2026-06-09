package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.model.InstalledApp
import com.calculator.vault.privacy.domain.model.VaultApp
import com.calculator.vault.privacy.domain.usecases.GetInstalledAppsUseCase
import com.calculator.vault.privacy.domain.usecases.GetVaultAppsUseCase
import com.calculator.vault.privacy.domain.usecases.LaunchAppUseCase
import com.calculator.vault.privacy.domain.usecases.ToggleAppFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsUiState(
    val loading: Boolean = true,
    val query: String = "",
    val vaultApps: List<VaultApp> = emptyList(),
    val installedApps: List<InstalledApp> = emptyList(),
    val showPicker: Boolean = false,
)

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getVaultAppsUseCase: GetVaultAppsUseCase,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val launchAppUseCase: LaunchAppUseCase,
    private val toggleAppFavoriteUseCase: ToggleAppFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
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
            val query = _uiState.value.query
            _uiState.update {
                it.copy(
                    loading = false,
                    vaultApps = getVaultAppsUseCase.execute(query),
                    installedApps = getInstalledAppsUseCase.execute(query),
                )
            }
        }
    }

    fun showPicker(show: Boolean) {
        _uiState.update { it.copy(showPicker = show) }
    }

    fun launchVaultApp(app: VaultApp) {
        viewModelScope.launch {
            launchAppUseCase.executeVaultApp(app)
            refresh()
        }
    }

    fun launchInstalledApp(app: InstalledApp) {
        viewModelScope.launch {
            launchAppUseCase.execute(app)
            _uiState.update { it.copy(showPicker = false) }
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
