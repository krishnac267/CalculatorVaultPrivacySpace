package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.AppRepository;
import com.calculator.vault.privacy.domain.model.VaultApp;

import java.util.List;

import javax.inject.Inject;

public final class GetVaultAppsUseCase {
    private final AppRepository appRepository;

    @Inject
    public GetVaultAppsUseCase(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public List<VaultApp> execute(String query) {
        if (query == null || query.isBlank()) {
            return appRepository.getAllApps();
        }
        return appRepository.searchApps(query);
    }
}
