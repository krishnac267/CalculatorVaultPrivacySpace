package com.calculator.vault.privacy.data.storage;

import android.content.Context;
import android.net.Uri;

import com.calculator.vault.privacy.core.security.ContentEncryptionService;
import com.calculator.vault.privacy.data.database.dao.VaultFileDao;
import com.calculator.vault.privacy.data.database.entity.VaultFileEntity;
import com.calculator.vault.privacy.domain.model.VaultFileCategory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class FileStorageManager {
    private static final long MAX_IMPORT_BYTES = 100L * 1024L * 1024L;
    private static final String VAULT_DIR = "vault";

    private final Context context;
    private final ContentEncryptionService encryptionService;
    private final VaultFileDao vaultFileDao;

    @Inject
    public FileStorageManager(
            @ApplicationContext Context context,
            ContentEncryptionService encryptionService,
            VaultFileDao vaultFileDao
    ) {
        this.context = context;
        this.encryptionService = encryptionService;
        this.vaultFileDao = vaultFileDao;
    }

    public File getVaultDirectory() {
        File dir = new File(context.getFilesDir(), VAULT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public ImportResult importFromUri(Uri uri, String displayName, String mimeType) throws IOException {
        long size = querySize(uri);
        if (size > MAX_IMPORT_BYTES) {
            throw new IOException("File exceeds 100 MB vault limit");
        }
        String internalName = UUID.randomUUID().toString() + ".enc";
        File target = new File(getVaultDirectory(), internalName);
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new IOException("Unable to read selected file");
            }
            try (FileOutputStream output = new FileOutputStream(target)) {
                encryptionService.encryptStream(input, output);
            }
        }
        long storedSize = target.length();
        VaultFileCategory category = VaultFileCategory.fromMimeType(mimeType);
        return new ImportResult(internalName, storedSize, category);
    }

    public File getEncryptedFile(String internalFileName) {
        return new File(getVaultDirectory(), internalFileName);
    }

    public File decryptToCache(VaultFileEntity entity) throws IOException {
        File encrypted = getEncryptedFile(entity.internalFileName);
        if (!encrypted.exists()) {
            throw new IOException("Vault file missing");
        }
        File cacheDir = new File(context.getCacheDir(), "vault_preview");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        String extension = extensionFor(entity.mimeType, entity.displayName);
        File preview = new File(cacheDir, "preview_" + entity.id + extension);
        try (FileInputStream input = new FileInputStream(encrypted);
             FileOutputStream output = new FileOutputStream(preview)) {
            encryptionService.decryptStream(input, output);
        }
        return preview;
    }

    public void deleteEncryptedFile(String internalFileName) {
        if (internalFileName == null || internalFileName.isEmpty()) return;
        File file = getEncryptedFile(internalFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public void purgeDeletedFiles(int vaultScope) {
        List<VaultFileEntity> deleted = vaultFileDao.getDeleted(vaultScope);
        for (VaultFileEntity entity : deleted) {
            deleteEncryptedFile(entity.internalFileName);
            vaultFileDao.hardDelete(entity.id, vaultScope);
        }
    }

    public void reencryptAllFiles(int fromVersion, int ignoredToVersion) {
        List<VaultFileEntity> files = vaultFileDao.getAllIncludingDeleted();
        for (VaultFileEntity entity : files) {
            if (entity.internalFileName == null || entity.internalFileName.isEmpty()) continue;
            File encrypted = getEncryptedFile(entity.internalFileName);
            if (!encrypted.exists()) continue;
            File tempPlain = new File(context.getCacheDir(), "rotate_" + entity.id + ".tmp");
            File tempEnc = new File(getVaultDirectory(), entity.internalFileName + ".new");
            try (FileInputStream encIn = new FileInputStream(encrypted);
                 FileOutputStream plainOut = new FileOutputStream(tempPlain)) {
                encryptionService.decryptStream(encIn, plainOut, fromVersion);
            } catch (IOException ignored) {
                tempPlain.delete();
                continue;
            }
            try (FileInputStream plainIn = new FileInputStream(tempPlain);
                 FileOutputStream encOut = new FileOutputStream(tempEnc)) {
                encryptionService.encryptStream(plainIn, encOut);
            } catch (IOException ignored) {
                tempPlain.delete();
                tempEnc.delete();
                continue;
            }
            tempPlain.delete();
            if (!encrypted.delete() || !tempEnc.renameTo(encrypted)) {
                tempEnc.delete();
            }
        }
    }

    public long getVaultDirectorySize() {
        File dir = getVaultDirectory();
        File[] files = dir.listFiles();
        if (files == null) return 0L;
        long total = 0L;
        for (File file : files) {
            total += file.length();
        }
        return total;
    }

    public void clearPreviewCache() {
        File cacheDir = new File(context.getCacheDir(), "vault_preview");
        if (!cacheDir.exists()) return;
        File[] files = cacheDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            file.delete();
        }
    }

    private long querySize(Uri uri) throws IOException {
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) return 0L;
            long total = 0L;
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > MAX_IMPORT_BYTES) {
                    return total;
                }
            }
            return total;
        }
    }

    private String extensionFor(String mimeType, String displayName) {
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) return ".img";
            if (mimeType.startsWith("video/")) return ".vid";
            if ("application/pdf".equals(mimeType)) return ".pdf";
        }
        if (displayName != null && displayName.contains(".")) {
            return displayName.substring(displayName.lastIndexOf('.')).toLowerCase(Locale.US);
        }
        return ".bin";
    }

    public static final class ImportResult {
        public final String internalFileName;
        public final long sizeBytes;
        public final VaultFileCategory category;

        public ImportResult(String internalFileName, long sizeBytes, VaultFileCategory category) {
            this.internalFileName = internalFileName;
            this.sizeBytes = sizeBytes;
            this.category = category;
        }
    }
}
