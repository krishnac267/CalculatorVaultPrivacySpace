package com.calculator.vault.privacy.data.repositories;

import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.data.database.dao.VaultNotificationDao;
import com.calculator.vault.privacy.data.database.entity.VaultNotificationEntity;
import com.calculator.vault.privacy.data.datasource.EntityMapper;
import com.calculator.vault.privacy.domain.interfaces.NotificationRepository;
import com.calculator.vault.privacy.domain.model.VaultNotification;
import com.calculator.vault.privacy.domain.model.VaultScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class NotificationRepositoryImpl implements NotificationRepository {
    private static final int MAX_HISTORY = 500;

    private final VaultNotificationDao vaultNotificationDao;
    private final SessionManager sessionManager;

    @Inject
    public NotificationRepositoryImpl(VaultNotificationDao vaultNotificationDao, SessionManager sessionManager) {
        this.vaultNotificationDao = vaultNotificationDao;
        this.sessionManager = sessionManager;
    }

    private int scope() {
        return sessionManager.getCurrentVaultScope();
    }

    @Override
    public List<VaultNotification> getAllNotifications() {
        return EntityMapper.toNotifications(vaultNotificationDao.getAll(scope()));
    }

    @Override
    public List<VaultNotification> searchNotifications(String query) {
        if (query == null || query.isBlank()) return getAllNotifications();
        return EntityMapper.toNotifications(vaultNotificationDao.search(scope(), query.trim()));
    }

    @Override
    public List<VaultNotification> getByPackage(String packageName) {
        return EntityMapper.toNotifications(vaultNotificationDao.getByPackage(scope(), packageName));
    }

    @Override
    public Map<String, Integer> getUnreadCountByPackage() {
        List<VaultNotificationDao.PackageUnreadCount> rows = vaultNotificationDao.unreadByPackage(scope());
        Map<String, Integer> map = new HashMap<>();
        for (VaultNotificationDao.PackageUnreadCount row : rows) {
            map.put(row.packageName, row.cnt);
        }
        return map;
    }

    @Override
    public void storeNotification(String packageName, String appLabel, String title, String body, long postedAt) {
        VaultNotificationEntity entity = new VaultNotificationEntity();
        entity.packageName = packageName;
        entity.appLabel = appLabel == null ? packageName : appLabel;
        entity.title = title == null ? "" : title;
        entity.body = body == null ? "" : body;
        entity.postedAt = postedAt;
        entity.read = false;
        entity.vaultScope = scope();
        vaultNotificationDao.insert(entity);
        trimHistory(MAX_HISTORY);
    }

    @Override
    public void markRead(long id) {
        vaultNotificationDao.markRead(id, scope());
    }

    @Override
    public void deleteNotification(long id) {
        vaultNotificationDao.delete(id, scope());
    }

    @Override
    public int getUnreadCount() {
        return vaultNotificationDao.unreadCount(scope());
    }

    @Override
    public int getNotificationCount() {
        return vaultNotificationDao.count(scope());
    }

    @Override
    public void trimHistory(int maxRows) {
        vaultNotificationDao.trimToMax(maxRows);
    }
}
