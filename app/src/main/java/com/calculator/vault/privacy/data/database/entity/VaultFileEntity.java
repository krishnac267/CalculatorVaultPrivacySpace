package com.calculator.vault.privacy.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vault_files")
public class VaultFileEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String displayName;
    public String mimeType;
    public String vaultPath;
    public String internalFileName;
    public String category;
    public boolean favorite;
    public boolean deleted;
    public long sizeBytes;
    public long importedAt;
    public int vaultScope;
}
