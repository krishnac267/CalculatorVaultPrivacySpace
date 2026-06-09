package com.calculator.vault.privacy.core.security;

import com.calculator.vault.privacy.data.database.dao.SecureNoteDao;
import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;
import com.calculator.vault.privacy.data.storage.FileStorageManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Rotates content encryption keys by re-encrypting note bodies and vault files
 * with a newly generated Keystore key.
 */
@Singleton
public final class KeyRotationManager {
    private static final String KEY_LAST_ROTATION = "content_key_last_rotation";

    private final PinManager pinManager;
    private final ContentEncryptionService encryptionService;
    private final SecureNoteDao secureNoteDao;
    private final FileStorageManager fileStorageManager;

    @Inject
    public KeyRotationManager(
            PinManager pinManager,
            ContentEncryptionService encryptionService,
            SecureNoteDao secureNoteDao,
            FileStorageManager fileStorageManager
    ) {
        this.pinManager = pinManager;
        this.encryptionService = encryptionService;
        this.secureNoteDao = secureNoteDao;
        this.fileStorageManager = fileStorageManager;
    }

    public int getActiveKeyVersion() {
        return encryptionService.getActiveKeyVersion();
    }

    public long getLastRotationAt() {
        return pinManager.getLong(KEY_LAST_ROTATION, 0L);
    }

    public synchronized void rotateContentKey() {
        int currentVersion = encryptionService.getActiveKeyVersion();
        int nextVersion = currentVersion + 1;
        encryptionService.ensureKey(nextVersion);

        List<SecureNoteEntity> notes = secureNoteDao.getAllEncryptedNotes();
        java.util.ArrayList<String> plaintexts = new java.util.ArrayList<>(notes.size());
        for (SecureNoteEntity note : notes) {
            if (note.encryptedContent == null || note.encryptedContent.isEmpty()) {
                plaintexts.add("");
                continue;
            }
            byte[] payload = android.util.Base64.decode(note.encryptedContent, android.util.Base64.NO_WRAP);
            plaintexts.add(new String(
                    encryptionService.decryptBytesWithVersion(payload, currentVersion),
                    java.nio.charset.StandardCharsets.UTF_8
            ));
        }

        encryptionService.setActiveKeyVersion(nextVersion);
        for (int i = 0; i < notes.size(); i++) {
            SecureNoteEntity note = notes.get(i);
            if (note.encryptedContent == null || note.encryptedContent.isEmpty()) continue;
            note.encryptedContent = encryptionService.encryptText(plaintexts.get(i));
            secureNoteDao.update(note);
        }

        fileStorageManager.reencryptAllFiles(currentVersion, nextVersion);
        pinManager.putLong(KEY_LAST_ROTATION, System.currentTimeMillis());
    }
}
