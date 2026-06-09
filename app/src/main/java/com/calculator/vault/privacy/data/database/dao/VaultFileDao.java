package com.calculator.vault.privacy.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.calculator.vault.privacy.data.database.entity.VaultFileEntity;

import java.util.List;

@Dao
public interface VaultFileDao {
    @Query("SELECT * FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0 ORDER BY importedAt DESC")
    List<VaultFileEntity> getAll(int vaultScope);

    @Query("SELECT * FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0 AND category = :category ORDER BY importedAt DESC")
    List<VaultFileEntity> getByCategory(int vaultScope, String category);

    @Query("SELECT * FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0 ORDER BY importedAt DESC LIMIT :limit")
    List<VaultFileEntity> getRecent(int vaultScope, int limit);

    @Query("SELECT * FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0 AND displayName LIKE '%' || :query || '%' ORDER BY importedAt DESC")
    List<VaultFileEntity> search(int vaultScope, String query);

    @Query("SELECT * FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0 AND favorite = 1 ORDER BY importedAt DESC")
    List<VaultFileEntity> getFavorites(int vaultScope);

    @Query("SELECT * FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 1 ORDER BY importedAt DESC")
    List<VaultFileEntity> getDeleted(int vaultScope);

    @Query("SELECT * FROM vault_files")
    List<VaultFileEntity> getAllIncludingDeleted();

    @Query("SELECT * FROM vault_files WHERE id = :id AND vaultScope = :vaultScope LIMIT 1")
    VaultFileEntity findById(long id, int vaultScope);

    @Query("SELECT COUNT(*) FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0")
    int count(int vaultScope);

    @Query("SELECT COALESCE(SUM(sizeBytes), 0) FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0")
    long totalSize(int vaultScope);

    @Query("SELECT COALESCE(SUM(sizeBytes), 0) FROM vault_files WHERE vaultScope = :vaultScope AND deleted = 0 AND category = :category")
    long totalSizeByCategory(int vaultScope, String category);

    @Insert
    long insert(VaultFileEntity entity);

    @Update
    void update(VaultFileEntity entity);

    @Query("UPDATE vault_files SET deleted = 1 WHERE id = :id AND vaultScope = :vaultScope")
    void softDelete(long id, int vaultScope);

    @Query("UPDATE vault_files SET deleted = 0 WHERE id = :id AND vaultScope = :vaultScope")
    void restore(long id, int vaultScope);

    @Query("UPDATE vault_files SET favorite = CASE favorite WHEN 1 THEN 0 ELSE 1 END WHERE id = :id AND vaultScope = :vaultScope")
    void toggleFavorite(long id, int vaultScope);

    @Query("DELETE FROM vault_files WHERE id = :id AND vaultScope = :vaultScope")
    void hardDelete(long id, int vaultScope);
}
