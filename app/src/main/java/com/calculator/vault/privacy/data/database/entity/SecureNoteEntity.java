package com.calculator.vault.privacy.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "secure_notes")
public class SecureNoteEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String title;
    public String content;
    public String encryptedContent;
    public String searchText;
    public boolean favorite;
    public boolean locked;
    public int vaultScope;
    public long createdAt;
    public long updatedAt;
}
