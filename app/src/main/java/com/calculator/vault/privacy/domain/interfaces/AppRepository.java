package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.VaultApp;

import java.util.List;

public interface AppRepository {
    List<VaultApp> getAllApps();
    List<VaultApp> getRecentApps(int limit);
    List<VaultApp> getFavoriteApps();
    List<VaultApp> searchApps(String query);
    void recordLaunch(String packageName, String label, String category);
    void toggleFavorite(long id);
    int getAppCount();
}
