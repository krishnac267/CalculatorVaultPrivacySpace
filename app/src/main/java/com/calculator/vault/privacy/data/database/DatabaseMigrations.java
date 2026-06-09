package com.calculator.vault.privacy.data.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public final class DatabaseMigrations {
    private DatabaseMigrations() {}

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE vault_apps ADD COLUMN vaultScope INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE secure_notes ADD COLUMN vaultScope INTEGER NOT NULL DEFAULT 0");
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS intruder_logs ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                            + "timestamp INTEGER NOT NULL, "
                            + "attemptCount INTEGER NOT NULL, "
                            + "detail TEXT)"
            );
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE intruder_logs ADD COLUMN photoPath TEXT");
            db.execSQL("ALTER TABLE vault_notifications ADD COLUMN vaultScope INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE vault_files ADD COLUMN vaultScope INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE secure_notes ADD COLUMN encryptedContent TEXT");
            db.execSQL("ALTER TABLE secure_notes ADD COLUMN searchText TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE vault_files ADD COLUMN category TEXT NOT NULL DEFAULT 'DOCUMENT'");
            db.execSQL("ALTER TABLE vault_files ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE vault_files ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE vault_files ADD COLUMN internalFileName TEXT NOT NULL DEFAULT ''");
        }
    };
}
