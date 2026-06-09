package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.DashboardRepository;
import com.calculator.vault.privacy.domain.model.DashboardSummary;

import javax.inject.Inject;

public final class LoadDashboardUseCase {
    private final DashboardRepository dashboardRepository;

    @Inject
    public LoadDashboardUseCase(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardSummary execute() {
        return dashboardRepository.loadSummary();
    }
}
