package com.calculator.vault.privacy.domain.model;

public final class VaultApp {
    private final long id;
    private final String packageName;
    private final String label;
    private final String category;
    private final boolean favorite;
    private final long lastLaunchedAt;
    private final int launchCount;
    private final boolean clone;

    public VaultApp(
            long id,
            String packageName,
            String label,
            String category,
            boolean favorite,
            long lastLaunchedAt,
            int launchCount,
            boolean clone
    ) {
        this.id = id;
        this.packageName = packageName;
        this.label = label;
        this.category = category;
        this.favorite = favorite;
        this.lastLaunchedAt = lastLaunchedAt;
        this.launchCount = launchCount;
        this.clone = clone;
    }

    public long getId() { return id; }
    public String getPackageName() { return packageName; }
    public String getLabel() { return label; }
    public String getCategory() { return category; }
    public boolean isFavorite() { return favorite; }
    public long getLastLaunchedAt() { return lastLaunchedAt; }
    public int getLaunchCount() { return launchCount; }
    public boolean isClone() { return clone; }
}
