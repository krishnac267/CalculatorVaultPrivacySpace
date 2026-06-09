package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.DashboardSummary;

public interface DashboardRepository {
    DashboardSummary loadSummary();
}
