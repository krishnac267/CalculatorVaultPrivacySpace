package com.calculator.vault.privacy.domain.usecases;

import android.net.Uri;

import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.model.VaultFile;
import com.calculator.vault.privacy.domain.model.VaultFileCategory;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public final class GetVaultFilesUseCase {
    private final FileRepository fileRepository;

    @Inject
    public GetVaultFilesUseCase(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<VaultFile> execute(VaultFileCategory category) {
        if (category == null) {
            return fileRepository.getAllFiles();
        }
        return fileRepository.getFilesByCategory(category);
    }

    public List<VaultFile> recent(int limit) {
        return fileRepository.getRecentFiles(limit);
    }

    public List<VaultFile> favorites() {
        return fileRepository.getFavoriteFiles();
    }

    public List<VaultFile> deleted() {
        return fileRepository.getDeletedFiles();
    }

    public List<VaultFile> search(String query) {
        return fileRepository.searchFiles(query);
    }
}
