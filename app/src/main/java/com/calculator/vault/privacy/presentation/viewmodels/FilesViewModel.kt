package com.calculator.vault.privacy.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calculator.vault.privacy.domain.model.VaultFile
import com.calculator.vault.privacy.domain.model.VaultFileCategory
import com.calculator.vault.privacy.domain.usecases.GetVaultFilesUseCase
import com.calculator.vault.privacy.domain.usecases.ImportVaultFileUseCase
import com.calculator.vault.privacy.domain.usecases.ManageVaultFileUseCase
import com.calculator.vault.privacy.domain.usecases.GetVaultFilePreviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilesTab {
    IMAGES,
    VIDEOS,
    DOCUMENTS,
    RECENT,
}

data class FilePreviewState(
    val name: String,
    val mimeType: String,
    val file: java.io.File?,
)

data class FilesUiState(
    val selectedTab: FilesTab = FilesTab.RECENT,
    val query: String = "",
    val files: List<VaultFile> = emptyList(),
    val loading: Boolean = true,
    val importing: Boolean = false,
    val error: String? = null,
    val showDeleted: Boolean = false,
    val imageBytes: Long = 0L,
    val videoBytes: Long = 0L,
    val documentBytes: Long = 0L,
    val totalBytes: Long = 0L,
)

@HiltViewModel
class FilesViewModel @Inject constructor(
    private val getVaultFilesUseCase: GetVaultFilesUseCase,
    private val importVaultFileUseCase: ImportVaultFileUseCase,
    private val manageVaultFileUseCase: ManageVaultFileUseCase,
    private val getVaultFilePreviewUseCase: GetVaultFilePreviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilesUiState())
    val uiState = _uiState.asStateFlow()

    private val _preview = MutableStateFlow<FilePreviewState?>(null)
    val preview = _preview.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val state = _uiState.value
                val files = when {
                    state.showDeleted -> getVaultFilesUseCase.deleted()
                    state.query.isNotBlank() -> getVaultFilesUseCase.search(state.query)
                    else -> loadTab(state.selectedTab)
                }
                _uiState.update {
                    it.copy(
                        loading = false,
                        files = files,
                        imageBytes = manageVaultFileUseCase.storageForCategory(VaultFileCategory.IMAGE),
                        videoBytes = manageVaultFileUseCase.storageForCategory(VaultFileCategory.VIDEO),
                        documentBytes = manageVaultFileUseCase.storageForCategory(VaultFileCategory.PDF)
                                + manageVaultFileUseCase.storageForCategory(VaultFileCategory.DOCUMENT),
                        totalBytes = manageVaultFileUseCase.totalStorage(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message ?: "Unable to load files") }
            }
        }
    }

    fun selectTab(tab: FilesTab) {
        _uiState.update { it.copy(selectedTab = tab, showDeleted = false) }
        refresh()
    }

    fun updateQuery(value: String) {
        _uiState.update { it.copy(query = value) }
        refresh()
    }

    fun toggleDeletedView() {
        _uiState.update { it.copy(showDeleted = !it.showDeleted) }
        refresh()
    }

    fun importFile(uri: Uri, displayName: String?, mimeType: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(importing = true, error = null) }
            try {
                importVaultFileUseCase.execute(
                    uri,
                    displayName ?: "Imported file",
                    mimeType ?: "application/octet-stream",
                )
                _uiState.update { it.copy(importing = false) }
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(importing = false, error = e.message ?: "Import failed")
                }
            }
        }
    }

    fun deleteFile(id: Long) {
        viewModelScope.launch {
            manageVaultFileUseCase.delete(id)
            refresh()
        }
    }

    fun restoreFile(id: Long) {
        viewModelScope.launch {
            manageVaultFileUseCase.restore(id)
            refresh()
        }
    }

    fun permanentlyDeleteFile(id: Long) {
        viewModelScope.launch {
            manageVaultFileUseCase.permanentlyDelete(id)
            refresh()
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            manageVaultFileUseCase.toggleFavorite(id)
            refresh()
        }
    }

    fun requestPreview(id: Long) {
        viewModelScope.launch {
            val meta = _uiState.value.files.find { it.id == id }
            try {
                val file = getVaultFilePreviewUseCase.execute(id)
                _preview.value = FilePreviewState(
                    name = meta?.displayName ?: "File",
                    mimeType = meta?.mimeType ?: "*/*",
                    file = file,
                )
            } catch (e: Exception) {
                _preview.value = FilePreviewState(
                    name = meta?.displayName ?: "File",
                    mimeType = meta?.mimeType ?: "*/*",
                    file = null,
                )
            }
        }
    }

    fun dismissPreview() {
        _preview.value = null
    }

    private fun loadTab(tab: FilesTab): List<VaultFile> = when (tab) {
        FilesTab.IMAGES -> getVaultFilesUseCase.execute(VaultFileCategory.IMAGE)
        FilesTab.VIDEOS -> getVaultFilesUseCase.execute(VaultFileCategory.VIDEO)
        FilesTab.DOCUMENTS -> getVaultFilesUseCase.execute(VaultFileCategory.PDF) +
                getVaultFilesUseCase.execute(VaultFileCategory.DOCUMENT)
        FilesTab.RECENT -> getVaultFilesUseCase.recent(50)
    }
}
