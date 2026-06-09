package com.calculator.vault.privacy.data.repositories;

import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.data.database.dao.VaultAppDao;
import com.calculator.vault.privacy.data.database.entity.VaultAppEntity;
import com.calculator.vault.privacy.data.datasource.EntityMapper;
import com.calculator.vault.privacy.domain.interfaces.AppRepository;
import com.calculator.vault.privacy.domain.model.VaultApp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AppRepositoryImpl implements AppRepository {
    private final VaultAppDao vaultAppDao;
    private final SessionManager sessionManager;

    @Inject
    public AppRepositoryImpl(VaultAppDao vaultAppDao, SessionManager sessionManager) {
        this.vaultAppDao = vaultAppDao;
        this.sessionManager = sessionManager;
    }

    private int scope() {
        return sessionManager.getCurrentVaultScope();
    }

    @Override
    public List<VaultApp> getAllApps() {
        return EntityMapper.toVaultApps(vaultAppDao.getAll(scope()));
    }

    @Override
    public List<VaultApp> getRecentApps(int limit) {
        return EntityMapper.toVaultApps(vaultAppDao.getRecent(scope(), limit));
    }

    @Override
    public List<VaultApp> getFavoriteApps() {
        return EntityMapper.toVaultApps(vaultAppDao.getFavorites(scope()));
    }

    @Override
    public List<VaultApp> searchApps(String query) {
        if (query == null || query.isBlank()) return getAllApps();
        return EntityMapper.toVaultApps(vaultAppDao.search(scope(), query.trim()));
    }

    @Override
    public void recordLaunch(String packageName, String label, String category) {
        recordLaunch(packageName, label, category, false);
    }

    @Override
    public void recordLaunch(String packageName, String label, String category, boolean clone) {
        VaultAppEntity existing = vaultAppDao.findByPackage(packageName, scope(), clone);
        long now = System.currentTimeMillis();
        if (existing == null) {
            VaultAppEntity entity = new VaultAppEntity();
            entity.packageName = packageName;
            entity.label = label;
            entity.category = category == null ? "Other" : category;
            entity.favorite = false;
            entity.lastLaunchedAt = now;
            entity.launchCount = 1;
            entity.vaultScope = scope();
            entity.isClone = clone;
            vaultAppDao.insert(entity);
        } else {
            existing.label = label;
            existing.lastLaunchedAt = now;
            existing.launchCount = existing.launchCount + 1;
            vaultAppDao.update(existing);
        }
    }

    @Override
    public void toggleFavorite(long id) {
        vaultAppDao.toggleFavorite(id, scope());
    }

    @Override
    public int getAppCount() {
        return vaultAppDao.count(scope());
    }
}
