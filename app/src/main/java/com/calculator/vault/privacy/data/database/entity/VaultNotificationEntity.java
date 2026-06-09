package com.calculator.vault.privacy.data.database.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "vault_notifications", indices = {@Index("packageName")})
public class VaultNotificationEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String packageName;
    public String appLabel;
    public String title;
    public String body;
    public long postedAt;
    public boolean read;
    public int vaultScope;
}
