package com.calculator.vault.privacy.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "intruder_logs")
public class IntruderLogEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long timestamp;
    public int attemptCount;
    public String detail;
    public String photoPath;
}
