package com.calculator.vault.privacy.core.session;

import com.calculator.vault.privacy.core.security.ContentEncryptionService;
import com.calculator.vault.privacy.core.security.PinManager;
import com.calculator.vault.privacy.data.database.dao.SecureNoteDao;
import com.calculator.vault.privacy.data.database.dao.VaultAppDao;
import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;
import com.calculator.vault.privacy.data.database.entity.VaultAppEntity;
import com.calculator.vault.privacy.data.migration.NoteEncryptionMigrator;
import com.calculator.vault.privacy.domain.model.VaultScope;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class FakeVaultSeeder {
    private final SecureNoteDao secureNoteDao;
    private final VaultAppDao vaultAppDao;
    private final PinManager pinManager;
    private final ContentEncryptionService encryptionService;

    @Inject
    public FakeVaultSeeder(
            SecureNoteDao secureNoteDao,
            VaultAppDao vaultAppDao,
            PinManager pinManager,
            ContentEncryptionService encryptionService
    ) {
        this.secureNoteDao = secureNoteDao;
        this.vaultAppDao = vaultAppDao;
        this.pinManager = pinManager;
        this.encryptionService = encryptionService;
    }

    public void seedIfEmpty() {
        if (secureNoteDao.count(VaultScope.FAKE) > 0) return;
        long now = System.currentTimeMillis();
        insertNote("Shopping List", "Milk, eggs, bread, coffee", now - 86_400_000L);
        insertNote("Weekend Plans", "Brunch at 11am, movie at 3pm", now - 172_800_000L);
        insertNote("Gift Ideas", "Book, candle, photo frame", now - 259_200_000L);

        VaultAppEntity app = new VaultAppEntity();
        app.packageName = "com.example.notes";
        app.label = "Notes";
        app.category = "Productivity";
        app.favorite = true;
        app.lastLaunchedAt = now - 3_600_000L;
        app.launchCount = 2;
        app.vaultScope = VaultScope.FAKE;
        vaultAppDao.insert(app);
        pinManager.putBoolean("fake_vault_seeded", true);
    }

    private void insertNote(String title, String content, long updatedAt) {
        SecureNoteEntity entity = new SecureNoteEntity();
        entity.title = title;
        entity.content = "";
        entity.encryptedContent = encryptionService.encryptText(content);
        entity.searchText = NoteEncryptionMigrator.buildSearchText(title, content);
        entity.favorite = false;
        entity.locked = false;
        entity.vaultScope = VaultScope.FAKE;
        entity.createdAt = updatedAt;
        entity.updatedAt = updatedAt;
        secureNoteDao.insert(entity);
    }
}
