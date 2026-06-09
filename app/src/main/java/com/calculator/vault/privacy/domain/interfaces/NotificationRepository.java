package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.VaultNotification;

import java.util.List;
import java.util.Map;

public interface NotificationRepository {
    List<VaultNotification> getAllNotifications();
    List<VaultNotification> searchNotifications(String query);
    List<VaultNotification> getByPackage(String packageName);
    Map<String, Integer> getUnreadCountByPackage();
    void storeNotification(String packageName, String appLabel, String title, String body, long postedAt);
    void markRead(long id);
    void deleteNotification(long id);
    int getUnreadCount();
    int getNotificationCount();
    void trimHistory(int maxRows);
}
