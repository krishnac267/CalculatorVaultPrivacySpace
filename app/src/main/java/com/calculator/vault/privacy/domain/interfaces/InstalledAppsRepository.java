package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.InstalledApp;

import java.util.List;

public interface InstalledAppsRepository {
    List<InstalledApp> getLaunchableApps();
    List<InstalledApp> searchApps(String query);
}
