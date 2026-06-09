package com.calculator.vault.privacy.migration

import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.calculator.vault.privacy.data.database.DatabaseMigrations
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationInstrumentedTest {
    private val testDb = "migration-test"

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(testDb)
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(testDb)
    }

    private fun createDatabaseAtVersion(version: Int, setup: SupportSQLiteDatabase.() -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(testDb)
            .callback(object : SupportSQLiteOpenHelper.Callback(version) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    setup(db)
                }

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int,
                ) = Unit
            })
            .build()
        FrameworkSQLiteOpenHelperFactory().create(config).writableDatabase.close()
    }

    private fun runMigrations(vararg migrations: androidx.room.migration.Migration) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(testDb)
            .callback(object : SupportSQLiteOpenHelper.Callback(4) {
                override fun onCreate(db: SupportSQLiteDatabase) = Unit

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int,
                ) = Unit
            })
            .build()
        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val db = helper.writableDatabase
        for (migration in migrations) {
            migration.migrate(db)
        }
        db.close()
    }

    private fun openMigratedDb(): SQLiteDatabase {
        val path = InstrumentationRegistry.getInstrumentation().targetContext
            .getDatabasePath(testDb)
            .path
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    }

    @Test
    fun migrate1To4_preservesNoteRow() {
        createDatabaseAtVersion(1) {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS vault_apps (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    packageName TEXT,
                    label TEXT,
                    category TEXT,
                    favorite INTEGER NOT NULL,
                    lastLaunchedAt INTEGER NOT NULL,
                    launchCount INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS secure_notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT,
                    content TEXT,
                    favorite INTEGER NOT NULL,
                    locked INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS vault_notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    packageName TEXT,
                    appLabel TEXT,
                    title TEXT,
                    body TEXT,
                    postedAt INTEGER NOT NULL,
                    read INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS vault_files (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    displayName TEXT,
                    mimeType TEXT,
                    vaultPath TEXT,
                    sizeBytes INTEGER NOT NULL,
                    importedAt INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            execSQL(
                """
                INSERT INTO secure_notes (title, content, favorite, locked, createdAt, updatedAt)
                VALUES ('Secret', 'plaintext-body', 0, 0, 1, 1)
                """.trimIndent(),
            )
        }
        runMigrations(
            DatabaseMigrations.MIGRATION_1_2,
            DatabaseMigrations.MIGRATION_2_3,
            DatabaseMigrations.MIGRATION_3_4,
        )
        openMigratedDb().use { db ->
            db.rawQuery("SELECT title, content, encryptedContent, searchText FROM secure_notes", null).use { cursor ->
                assertThat(cursor.moveToFirst()).isTrue()
                assertThat(cursor.getString(0)).isEqualTo("Secret")
                assertThat(cursor.getString(1)).isEqualTo("plaintext-body")
                assertThat(cursor.isNull(2)).isTrue()
                assertThat(cursor.getString(3)).isEmpty()
            }
        }
    }

    @Test
    fun migrate3To4_addsEncryptionColumns() {
        createDatabaseAtVersion(3) {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS secure_notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT,
                    content TEXT,
                    favorite INTEGER NOT NULL,
                    locked INTEGER NOT NULL,
                    vaultScope INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS vault_files (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    displayName TEXT,
                    mimeType TEXT,
                    vaultPath TEXT,
                    sizeBytes INTEGER NOT NULL,
                    importedAt INTEGER NOT NULL,
                    vaultScope INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent(),
            )
        }
        runMigrations(DatabaseMigrations.MIGRATION_3_4)
        openMigratedDb().use { db ->
            db.rawQuery("PRAGMA table_info(secure_notes)", null).use { cursor ->
                val columns = mutableSetOf<String>()
                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(1))
                }
                assertThat(columns).containsAtLeast("encryptedContent", "searchText")
            }
        }
    }
}
