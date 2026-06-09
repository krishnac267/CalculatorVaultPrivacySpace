package com.calculator.vault.privacy.domain.usecases;

import android.net.Uri;

import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.model.VaultFile;

import javax.inject.Inject;

public final class ImportVaultFileUseCase {
    private final FileRepository fileRepository;

    @Inject
    public ImportVaultFileUseCase(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public VaultFile execute(Uri uri, String displayName, String mimeType) {
        return fileRepository.importFile(uri, displayName, mimeType);
    }
}
