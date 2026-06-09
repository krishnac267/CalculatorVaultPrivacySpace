package com.calculator.vault.privacy.domain.model;

public final class IntruderLog {
    private final long id;
    private final long timestamp;
    private final int attemptCount;
    private final String detail;
    private final String photoPath;

    public IntruderLog(long id, long timestamp, int attemptCount, String detail, String photoPath) {
        this.id = id;
        this.timestamp = timestamp;
        this.attemptCount = attemptCount;
        this.detail = detail;
        this.photoPath = photoPath;
    }

    public long getId() { return id; }
    public long getTimestamp() { return timestamp; }
    public int getAttemptCount() { return attemptCount; }
    public String getDetail() { return detail; }
    public String getPhotoPath() { return photoPath; }
    public boolean hasPhoto() { return photoPath != null && !photoPath.isBlank(); }
}
