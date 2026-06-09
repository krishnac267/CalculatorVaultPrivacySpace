package com.calculator.vault.privacy.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.calculator.vault.privacy.data.database.entity.VaultAppEntity;

import java.util.List;

@Dao
public interface VaultAppDao {
    @Query("SELECT * FROM vault_apps WHERE vaultScope = :vaultScope ORDER BY lastLaunchedAt DESC")
    List<VaultAppEntity> getAll(int vaultScope);

    @Query("SELECT * FROM vault_apps WHERE vaultScope = :vaultScope ORDER BY lastLaunchedAt DESC LIMIT :limit")
    List<VaultAppEntity> getRecent(int vaultScope, int limit);

    @Query("SELECT * FROM vault_apps WHERE vaultScope = :vaultScope AND favorite = 1 ORDER BY label ASC")
    List<VaultAppEntity> getFavorites(int vaultScope);

    @Query("SELECT * FROM vault_apps WHERE vaultScope = :vaultScope AND (label LIKE '%' || :query || '%' OR packageName LIKE '%' || :query || '%')")
    List<VaultAppEntity> search(int vaultScope, String query);

    @Query("SELECT * FROM vault_apps WHERE packageName = :packageName AND vaultScope = :vaultScope AND isClone = :isClone LIMIT 1")
    VaultAppEntity findByPackage(String packageName, int vaultScope, boolean isClone);

    @Query("SELECT COUNT(*) FROM vault_apps WHERE vaultScope = :vaultScope")
    int count(int vaultScope);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(VaultAppEntity entity);

    @Update
    void update(VaultAppEntity entity);

    @Query("UPDATE vault_apps SET favorite = CASE favorite WHEN 1 THEN 0 ELSE 1 END WHERE id = :id AND vaultScope = :vaultScope")
    void toggleFavorite(long id, int vaultScope);
}
