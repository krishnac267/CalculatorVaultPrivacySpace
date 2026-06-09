package com.calculator.vault.privacy.data.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.calculator.vault.privacy.domain.interfaces.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VaultNotificationListenerService : NotificationListenerService() {

    @Inject lateinit var notificationRepository: NotificationRepository

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName == packageName) return
        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val body = extras.getCharSequence("android.text")?.toString() ?: ""
        if (title.isBlank() && body.isBlank()) return
        val appLabel = try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(sbn.packageName, 0),
            ).toString()
        } catch (_: Exception) {
            sbn.packageName
        }
        notificationRepository.storeNotification(
            sbn.packageName,
            appLabel,
            title,
            body,
            sbn.postTime,
        )
    }
}
