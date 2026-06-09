package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.FileRepository;

import java.io.File;

import javax.inject.Inject;

public final class GetVaultFilePreviewUseCase {
    private final FileRepository fileRepository;

    @Inject
    public GetVaultFilePreviewUseCase(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File execute(long id) {
        return fileRepository.getPreviewFile(id);
    }
}
