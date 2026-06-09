package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.AnalyticsRepository;
import com.calculator.vault.privacy.domain.model.SecurityAnalytics;

import javax.inject.Inject;

public final class LoadSecurityAnalyticsUseCase {
    private final AnalyticsRepository analyticsRepository;

    @Inject
    public LoadSecurityAnalyticsUseCase(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public SecurityAnalytics execute() {
        return analyticsRepository.loadSecurityAnalytics();
    }
}
