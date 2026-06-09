package com.calculator.vault.privacy.domain.model;

public final class StorageAnalytics {
    private final long usedBytes;
    private final long limitBytes;
    private final int noteCount;
    private final int noteLimit;
    private final int appCount;
    private final int appLimit;
    private final int fileCount;
    private final int notificationCount;
    private final boolean premium;
    private final int usagePercent;

    public StorageAnalytics(
            long usedBytes,
            long limitBytes,
            int noteCount,
            int noteLimit,
            int appCount,
            int appLimit,
            int fileCount,
            int notificationCount,
            boolean premium,
            int usagePercent
    ) {
        this.usedBytes = usedBytes;
        this.limitBytes = limitBytes;
        this.noteCount = noteCount;
        this.noteLimit = noteLimit;
        this.appCount = appCount;
        this.appLimit = appLimit;
        this.fileCount = fileCount;
        this.notificationCount = notificationCount;
        this.premium = premium;
        this.usagePercent = usagePercent;
    }

    public long getUsedBytes() { return usedBytes; }
    public long getLimitBytes() { return limitBytes; }
    public int getNoteCount() { return noteCount; }
    public int getNoteLimit() { return noteLimit; }
    public int getAppCount() { return appCount; }
    public int getAppLimit() { return appLimit; }
    public int getFileCount() { return fileCount; }
    public int getNotificationCount() { return notificationCount; }
    public boolean isPremium() { return premium; }
    public int getUsagePercent() { return usagePercent; }
}
