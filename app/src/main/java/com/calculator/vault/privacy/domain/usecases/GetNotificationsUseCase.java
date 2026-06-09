package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NotificationRepository;
import com.calculator.vault.privacy.domain.model.VaultNotification;

import java.util.List;

import javax.inject.Inject;

public final class GetNotificationsUseCase {
    private final NotificationRepository notificationRepository;

    @Inject
    public GetNotificationsUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<VaultNotification> execute(String query) {
        if (query == null || query.isBlank()) {
            return notificationRepository.getAllNotifications();
        }
        return notificationRepository.searchNotifications(query);
    }
}
