package com.calculator.vault.privacy.domain.model;

public final class VaultNotification {
    private final long id;
    private final String packageName;
    private final String appLabel;
    private final String title;
    private final String body;
    private final long postedAt;
    private final boolean read;

    public VaultNotification(
            long id,
            String packageName,
            String appLabel,
            String title,
            String body,
            long postedAt,
            boolean read
    ) {
        this.id = id;
        this.packageName = packageName;
        this.appLabel = appLabel;
        this.title = title;
        this.body = body;
        this.postedAt = postedAt;
        this.read = read;
    }

    public long getId() { return id; }
    public String getPackageName() { return packageName; }
    public String getAppLabel() { return appLabel; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public long getPostedAt() { return postedAt; }
    public boolean isRead() { return read; }
}
