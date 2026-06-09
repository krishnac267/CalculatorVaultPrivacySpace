package com.calculator.vault.privacy.domain.model;

import java.util.Collections;
import java.util.List;

public final class DashboardSummary {
    private final SessionState sessionState;
    private final long lastLoginAt;
    private final int noteCount;
    private final int fileCount;
    private final int notificationCount;
    private final int appCount;
    private final long storageUsedBytes;
    private final List<VaultApp> recentApps;
    private final List<VaultApp> favoriteApps;
    private final List<SecureNote> recentNotes;

    public DashboardSummary(
            SessionState sessionState,
            long lastLoginAt,
            int noteCount,
            int fileCount,
            int notificationCount,
            int appCount,
            long storageUsedBytes,
            List<VaultApp> recentApps,
            List<VaultApp> favoriteApps,
            List<SecureNote> recentNotes
    ) {
        this.sessionState = sessionState;
        this.lastLoginAt = lastLoginAt;
        this.noteCount = noteCount;
        this.fileCount = fileCount;
        this.notificationCount = notificationCount;
        this.appCount = appCount;
        this.storageUsedBytes = storageUsedBytes;
        this.recentApps = recentApps == null ? Collections.emptyList() : recentApps;
        this.favoriteApps = favoriteApps == null ? Collections.emptyList() : favoriteApps;
        this.recentNotes = recentNotes == null ? Collections.emptyList() : recentNotes;
    }

    public SessionState getSessionState() { return sessionState; }
    public long getLastLoginAt() { return lastLoginAt; }
    public int getNoteCount() { return noteCount; }
    public int getFileCount() { return fileCount; }
    public int getNotificationCount() { return notificationCount; }
    public int getAppCount() { return appCount; }
    public long getStorageUsedBytes() { return storageUsedBytes; }
    public List<VaultApp> getRecentApps() { return recentApps; }
    public List<VaultApp> getFavoriteApps() { return favoriteApps; }
    public List<SecureNote> getRecentNotes() { return recentNotes; }
}
