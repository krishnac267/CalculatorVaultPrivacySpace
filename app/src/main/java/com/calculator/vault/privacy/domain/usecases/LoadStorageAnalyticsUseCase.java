package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.AnalyticsRepository;
import com.calculator.vault.privacy.domain.model.StorageAnalytics;

import javax.inject.Inject;

public final class LoadStorageAnalyticsUseCase {
    private final AnalyticsRepository analyticsRepository;

    @Inject
    public LoadStorageAnalyticsUseCase(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public StorageAnalytics execute() {
        return analyticsRepository.loadStorageAnalytics();
    }
}
