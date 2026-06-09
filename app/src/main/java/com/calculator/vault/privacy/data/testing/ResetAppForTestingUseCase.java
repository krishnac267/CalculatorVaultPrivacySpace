package com.calculator.vault.privacy.data.testing;

import android.content.Context;

import com.calculator.vault.privacy.core.security.PinManager;
import com.calculator.vault.privacy.core.session.NoteUnlockSession;
import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.data.database.PrivacySpaceDatabase;
import com.calculator.vault.privacy.data.storage.FileStorageManager;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/** Clears all local vault state — intended for instrumentation / E2E resets. */
@Singleton
public final class ResetAppForTestingUseCase {
    private final Context context;
    private final PrivacySpaceDatabase database;
    private final PinManager pinManager;
    private final SessionManager sessionManager;
    private final NoteUnlockSession noteUnlockSession;
    private final FileStorageManager fileStorageManager;

    @Inject
    public ResetAppForTestingUseCase(
            @ApplicationContext Context context,
            PrivacySpaceDatabase database,
            PinManager pinManager,
            SessionManager sessionManager,
            NoteUnlockSession noteUnlockSession,
            FileStorageManager fileStorageManager
    ) {
        this.context = context;
        this.database = database;
        this.pinManager = pinManager;
        this.sessionManager = sessionManager;
        this.noteUnlockSession = noteUnlockSession;
        this.fileStorageManager = fileStorageManager;
    }

    public void execute() {
        database.clearAllTables();
        pinManager.resetVaultStateForTesting();
        sessionManager.lock();
        noteUnlockSession.lockAll();
        fileStorageManager.clearPreviewCache();
        deleteRecursive(new File(context.getFilesDir(), "vault"));
    }

    private static void deleteRecursive(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }
}
