package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.model.VaultFileCategory;

import javax.inject.Inject;

public final class ManageVaultFileUseCase {
    private final FileRepository fileRepository;

    @Inject
    public ManageVaultFileUseCase(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void delete(long id) {
        fileRepository.deleteFile(id);
    }

    public void restore(long id) {
        fileRepository.restoreFile(id);
    }

    public void permanentlyDelete(long id) {
        fileRepository.permanentlyDeleteFile(id);
    }

    public void toggleFavorite(long id) {
        fileRepository.toggleFavorite(id);
    }

    public long storageForCategory(VaultFileCategory category) {
        return fileRepository.getStorageBytesByCategory(category);
    }

    public long totalStorage() {
        return fileRepository.getTotalStorageBytes();
    }
}
