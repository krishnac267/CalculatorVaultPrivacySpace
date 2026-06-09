package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.SecurityAnalytics;
import com.calculator.vault.privacy.domain.model.StorageAnalytics;

public interface AnalyticsRepository {
    SecurityAnalytics loadSecurityAnalytics();
    StorageAnalytics loadStorageAnalytics();
}
