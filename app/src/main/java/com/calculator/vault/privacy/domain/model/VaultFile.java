package com.calculator.vault.privacy.domain.model;



public final class VaultFile {

    private final long id;

    private final String displayName;

    private final String mimeType;

    private final VaultFileCategory category;

    private final boolean favorite;

    private final boolean deleted;

    private final long sizeBytes;

    private final long importedAt;



    public VaultFile(

            long id,

            String displayName,

            String mimeType,

            VaultFileCategory category,

            boolean favorite,

            boolean deleted,

            long sizeBytes,

            long importedAt

    ) {

        this.id = id;

        this.displayName = displayName;

        this.mimeType = mimeType;

        this.category = category;

        this.favorite = favorite;

        this.deleted = deleted;

        this.sizeBytes = sizeBytes;

        this.importedAt = importedAt;

    }



    public long getId() { return id; }

    public String getDisplayName() { return displayName; }

    public String getMimeType() { return mimeType; }

    public VaultFileCategory getCategory() { return category; }

    public boolean isFavorite() { return favorite; }

    public boolean isDeleted() { return deleted; }

    public long getSizeBytes() { return sizeBytes; }

    public long getImportedAt() { return importedAt; }

}

