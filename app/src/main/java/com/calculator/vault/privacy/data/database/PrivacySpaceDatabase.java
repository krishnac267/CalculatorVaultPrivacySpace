package com.calculator.vault.privacy.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.calculator.vault.privacy.data.database.dao.IntruderLogDao;
import com.calculator.vault.privacy.data.database.dao.SecureNoteDao;
import com.calculator.vault.privacy.data.database.dao.VaultAppDao;
import com.calculator.vault.privacy.data.database.dao.VaultFileDao;
import com.calculator.vault.privacy.data.database.dao.VaultNotificationDao;
import com.calculator.vault.privacy.data.database.entity.IntruderLogEntity;
import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;
import com.calculator.vault.privacy.data.database.entity.VaultAppEntity;
import com.calculator.vault.privacy.data.database.entity.VaultFileEntity;
import com.calculator.vault.privacy.data.database.entity.VaultNotificationEntity;

@Database(
        entities = {
                VaultAppEntity.class,
                SecureNoteEntity.class,
                VaultNotificationEntity.class,
                VaultFileEntity.class,
                IntruderLogEntity.class
        },
        version = 4,
        exportSchema = false
)
public abstract class PrivacySpaceDatabase extends RoomDatabase {
    public abstract VaultAppDao vaultAppDao();
    public abstract SecureNoteDao secureNoteDao();
    public abstract VaultNotificationDao vaultNotificationDao();
    public abstract VaultFileDao vaultFileDao();
    public abstract IntruderLogDao intruderLogDao();
}
