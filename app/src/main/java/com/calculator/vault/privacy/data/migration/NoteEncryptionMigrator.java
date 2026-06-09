package com.calculator.vault.privacy.data.migration;

import com.calculator.vault.privacy.core.security.ContentEncryptionService;
import com.calculator.vault.privacy.data.database.dao.SecureNoteDao;
import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class NoteEncryptionMigrator {
    private final SecureNoteDao secureNoteDao;
    private final ContentEncryptionService encryptionService;

    @Inject
    public NoteEncryptionMigrator(
            SecureNoteDao secureNoteDao,
            ContentEncryptionService encryptionService
    ) {
        this.secureNoteDao = secureNoteDao;
        this.encryptionService = encryptionService;
    }

    public void migrateIfNeeded() {
        List<SecureNoteEntity> pending = secureNoteDao.getNotesNeedingEncryption();
        for (SecureNoteEntity note : pending) {
            String plaintext = note.content == null ? "" : note.content;
            note.encryptedContent = encryptionService.encryptText(plaintext);
            note.searchText = buildSearchText(note.title, plaintext);
            note.content = "";
            secureNoteDao.update(note);
        }
    }

    public static String buildSearchText(String title, String content) {
        StringBuilder builder = new StringBuilder();
        if (title != null) {
            builder.append(title.toLowerCase(Locale.US));
        }
        if (content != null && !content.isEmpty()) {
            if (builder.length() > 0) builder.append(' ');
            builder.append(content.toLowerCase(Locale.US));
        }
        return builder.toString();
    }
}
