package com.calculator.vault.privacy.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.model.DashboardSummary
import com.calculator.vault.privacy.domain.model.SecurityAnalytics
import com.calculator.vault.privacy.domain.model.StorageAnalytics
import com.calculator.vault.privacy.domain.usecases.LoadDashboardUseCase
import com.calculator.vault.privacy.domain.usecases.LoadSecurityAnalyticsUseCase
import com.calculator.vault.privacy.domain.usecases.LoadStorageAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val loading: Boolean = true,
    val isRefreshing: Boolean = false,
    val summary: DashboardSummary? = null,
    val securityAnalytics: SecurityAnalytics? = null,
    val storageAnalytics: StorageAnalytics? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val loadDashboardUseCase: LoadDashboardUseCase,
    private val loadSecurityAnalyticsUseCase: LoadSecurityAnalyticsUseCase,
    private val loadStorageAnalyticsUseCase: LoadStorageAnalyticsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val hasData = _uiState.value.summary != null
            _uiState.update {
                if (hasData) it.copy(isRefreshing = true) else it.copy(loading = true)
            }
            val summary = loadDashboardUseCase.execute()
            val security = loadSecurityAnalyticsUseCase.execute()
            val storage = loadStorageAnalyticsUseCase.execute()
            _uiState.update {
                it.copy(
                    loading = false,
                    isRefreshing = false,
                    summary = summary,
                    securityAnalytics = security,
                    storageAnalytics = storage,
                )
            }
        }
    }
}
