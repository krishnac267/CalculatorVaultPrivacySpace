package com.calculator.vault.privacy.data.repositories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.data.database.dao.VaultFileDao;
import com.calculator.vault.privacy.data.database.entity.VaultFileEntity;
import com.calculator.vault.privacy.data.datasource.EntityMapper;
import com.calculator.vault.privacy.data.storage.FileStorageManager;
import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.model.VaultFile;
import com.calculator.vault.privacy.domain.model.VaultFileCategory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class FileRepositoryImpl implements FileRepository {
    private final VaultFileDao vaultFileDao;
    private final SessionManager sessionManager;
    private final FileStorageManager fileStorageManager;
    private final Context context;

    @Inject
    public FileRepositoryImpl(
            VaultFileDao vaultFileDao,
            SessionManager sessionManager,
            FileStorageManager fileStorageManager,
            @ApplicationContext Context context
    ) {
        this.vaultFileDao = vaultFileDao;
        this.sessionManager = sessionManager;
        this.fileStorageManager = fileStorageManager;
        this.context = context;
    }

    private int scope() {
        return sessionManager.getCurrentVaultScope();
    }

    @Override
    public List<VaultFile> getAllFiles() {
        return EntityMapper.toFiles(vaultFileDao.getAll(scope()));
    }

    @Override
    public List<VaultFile> getFilesByCategory(VaultFileCategory category) {
        return EntityMapper.toFiles(vaultFileDao.getByCategory(scope(), category.name()));
    }

    @Override
    public List<VaultFile> getRecentFiles(int limit) {
        return EntityMapper.toFiles(vaultFileDao.getRecent(scope(), limit));
    }

    @Override
    public List<VaultFile> getFavoriteFiles() {
        return EntityMapper.toFiles(vaultFileDao.getFavorites(scope()));
    }

    @Override
    public List<VaultFile> getDeletedFiles() {
        return EntityMapper.toFiles(vaultFileDao.getDeleted(scope()));
    }

    @Override
    public List<VaultFile> searchFiles(String query) {
        if (query == null || query.isBlank()) return getAllFiles();
        return EntityMapper.toFiles(vaultFileDao.search(scope(), query.trim()));
    }

    @Override
    public VaultFile importFile(Uri uri, String displayName, String mimeType) {
        try {
            String resolvedMime = mimeType == null ? context.getContentResolver().getType(uri) : mimeType;
            if (resolvedMime == null) resolvedMime = "application/octet-stream";
            String resolvedName = displayName;
            if (resolvedName == null || resolvedName.isBlank()) {
                resolvedName = queryDisplayName(uri);
            }
            FileStorageManager.ImportResult result =
                    fileStorageManager.importFromUri(uri, resolvedName, resolvedMime);
            VaultFileEntity entity = new VaultFileEntity();
            entity.displayName = resolvedName == null || resolvedName.isBlank()
                    ? "Imported file"
                    : resolvedName.trim();
            entity.mimeType = resolvedMime;
            entity.vaultPath = "";
            entity.internalFileName = result.internalFileName;
            entity.category = result.category.name();
            entity.favorite = false;
            entity.deleted = false;
            entity.sizeBytes = result.sizeBytes;
            entity.importedAt = System.currentTimeMillis();
            entity.vaultScope = scope();
            entity.id = vaultFileDao.insert(entity);
            return EntityMapper.toDomain(entity);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to import file", e);
        }
    }

    @Override
    public VaultFile getFile(long id) {
        VaultFileEntity entity = vaultFileDao.findById(id, scope());
        if (entity == null) throw new IllegalArgumentException("File not found");
        return EntityMapper.toDomain(entity);
    }

    @Override
    public void deleteFile(long id) {
        vaultFileDao.softDelete(id, scope());
    }

    @Override
    public void restoreFile(long id) {
        vaultFileDao.restore(id, scope());
    }

    @Override
    public void permanentlyDeleteFile(long id) {
        VaultFileEntity entity = vaultFileDao.findById(id, scope());
        if (entity != null) {
            fileStorageManager.deleteEncryptedFile(entity.internalFileName);
            vaultFileDao.hardDelete(id, scope());
        }
    }

    @Override
    public void toggleFavorite(long id) {
        vaultFileDao.toggleFavorite(id, scope());
    }

    @Override
    public File getPreviewFile(long id) {
        VaultFileEntity entity = vaultFileDao.findById(id, scope());
        if (entity == null) throw new IllegalArgumentException("File not found");
        try {
            return fileStorageManager.decryptToCache(entity);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to preview file", e);
        }
    }

    @Override
    public long getTotalStorageBytes() {
        return vaultFileDao.totalSize(scope());
    }

    @Override
    public long getStorageBytesByCategory(VaultFileCategory category) {
        return vaultFileDao.totalSizeByCategory(scope(), category.name());
    }

    @Override
    public int getFileCount() {
        return vaultFileDao.count(scope());
    }

    private String queryDisplayName(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) return "Imported file";
        try {
            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst() && index >= 0) {
                return cursor.getString(index);
            }
        } finally {
            cursor.close();
        }
        return "Imported file";
    }
}
