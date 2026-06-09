package com.calculator.vault.privacy.domain.model;

public enum VaultFileCategory {
    IMAGE,
    VIDEO,
    PDF,
    DOCUMENT;

    public static VaultFileCategory fromMimeType(String mimeType) {
        if (mimeType == null) return DOCUMENT;
        String type = mimeType.toLowerCase();
        if (type.startsWith("image/")) return IMAGE;
        if (type.startsWith("video/")) return VIDEO;
        if (type.equals("application/pdf")) return PDF;
        return DOCUMENT;
    }

    public static VaultFileCategory fromStorageValue(String value) {
        if (value == null) return DOCUMENT;
        try {
            return VaultFileCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            return DOCUMENT;
        }
    }
}
