package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.InstalledAppsRepository;
import com.calculator.vault.privacy.domain.model.InstalledApp;

import java.util.List;

import javax.inject.Inject;

public final class GetInstalledAppsUseCase {
    private final InstalledAppsRepository installedAppsRepository;

    @Inject
    public GetInstalledAppsUseCase(InstalledAppsRepository installedAppsRepository) {
        this.installedAppsRepository = installedAppsRepository;
    }

    public List<InstalledApp> execute(String query) {
        if (query == null || query.isBlank()) {
            return installedAppsRepository.getLaunchableApps();
        }
        return installedAppsRepository.searchApps(query);
    }
}
