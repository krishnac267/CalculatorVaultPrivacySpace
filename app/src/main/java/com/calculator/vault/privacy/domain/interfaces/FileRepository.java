package com.calculator.vault.privacy.domain.interfaces;



import android.net.Uri;



import com.calculator.vault.privacy.domain.model.VaultFile;

import com.calculator.vault.privacy.domain.model.VaultFileCategory;



import java.io.File;

import java.util.List;



public interface FileRepository {

    List<VaultFile> getAllFiles();

    List<VaultFile> getFilesByCategory(VaultFileCategory category);

    List<VaultFile> getRecentFiles(int limit);

    List<VaultFile> getFavoriteFiles();

    List<VaultFile> getDeletedFiles();

    List<VaultFile> searchFiles(String query);

    VaultFile importFile(Uri uri, String displayName, String mimeType);

    VaultFile getFile(long id);

    void deleteFile(long id);

    void restoreFile(long id);

    void permanentlyDeleteFile(long id);

    void toggleFavorite(long id);

    File getPreviewFile(long id);

    long getTotalStorageBytes();

    long getStorageBytesByCategory(VaultFileCategory category);

    int getFileCount();

}

