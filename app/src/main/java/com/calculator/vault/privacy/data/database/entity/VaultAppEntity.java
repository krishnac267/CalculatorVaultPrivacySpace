package com.calculator.vault.privacy.data.database.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "vault_apps", indices = {@Index("packageName")})
public class VaultAppEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String packageName;
    public String label;
    public String category;
    public boolean favorite;
    public long lastLaunchedAt;
    public int launchCount;
    public int vaultScope;
    public boolean isClone;
}
