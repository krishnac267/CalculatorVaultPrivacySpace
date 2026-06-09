package com.calculator.vault.privacy.data.datasource;

import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;
import com.calculator.vault.privacy.data.database.entity.VaultAppEntity;
import com.calculator.vault.privacy.data.database.entity.VaultFileEntity;
import com.calculator.vault.privacy.data.database.entity.VaultNotificationEntity;
import com.calculator.vault.privacy.domain.model.SecureNote;
import com.calculator.vault.privacy.domain.model.VaultApp;
import com.calculator.vault.privacy.domain.model.VaultFile;
import com.calculator.vault.privacy.domain.model.VaultFileCategory;
import com.calculator.vault.privacy.domain.model.VaultNotification;

import java.util.ArrayList;
import java.util.List;

public final class EntityMapper {
    private EntityMapper() {}

    public static VaultApp toDomain(VaultAppEntity entity) {
        return new VaultApp(
                entity.id,
                entity.packageName,
                entity.label,
                entity.category,
                entity.favorite,
                entity.lastLaunchedAt,
                entity.launchCount
        );
    }

    public static List<VaultApp> toVaultApps(List<VaultAppEntity> entities) {
        List<VaultApp> apps = new ArrayList<>(entities.size());
        for (VaultAppEntity entity : entities) {
            apps.add(toDomain(entity));
        }
        return apps;
    }

    public static SecureNote toDomain(SecureNoteEntity entity) {
        return new SecureNote(
                entity.id,
                entity.title,
                "",
                entity.favorite,
                entity.locked,
                true,
                entity.createdAt,
                entity.updatedAt
        );
    }

    public static List<SecureNote> toNotes(List<SecureNoteEntity> entities) {
        List<SecureNote> notes = new ArrayList<>(entities.size());
        for (SecureNoteEntity entity : entities) {
            notes.add(toDomain(entity));
        }
        return notes;
    }

    public static VaultNotification toDomain(VaultNotificationEntity entity) {
        return new VaultNotification(
                entity.id,
                entity.packageName,
                entity.appLabel,
                entity.title,
                entity.body,
                entity.postedAt,
                entity.read
        );
    }

    public static List<VaultNotification> toNotifications(List<VaultNotificationEntity> entities) {
        List<VaultNotification> notifications = new ArrayList<>(entities.size());
        for (VaultNotificationEntity entity : entities) {
            notifications.add(toDomain(entity));
        }
        return notifications;
    }

    public static VaultFile toDomain(VaultFileEntity entity) {
        return new VaultFile(
                entity.id,
                entity.displayName,
                entity.mimeType,
                VaultFileCategory.fromStorageValue(entity.category),
                entity.favorite,
                entity.deleted,
                entity.sizeBytes,
                entity.importedAt
        );
    }

    public static List<VaultFile> toFiles(List<VaultFileEntity> entities) {
        List<VaultFile> files = new ArrayList<>(entities.size());
        for (VaultFileEntity entity : entities) {
            files.add(toDomain(entity));
        }
        return files;
    }
}
