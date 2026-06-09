package com.calculator.vault.privacy.domain.model;

public final class InstalledApp {
    private final String packageName;
    private final String label;
    private final String category;

    public InstalledApp(String packageName, String label, String category) {
        this.packageName = packageName;
        this.label = label;
        this.category = category;
    }

    public String getPackageName() { return packageName; }
    public String getLabel() { return label; }
    public String getCategory() { return category; }
}
