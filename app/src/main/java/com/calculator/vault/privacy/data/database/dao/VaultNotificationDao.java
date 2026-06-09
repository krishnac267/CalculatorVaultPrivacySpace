package com.calculator.vault.privacy.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.calculator.vault.privacy.data.database.entity.VaultNotificationEntity;

import java.util.List;

@Dao
public interface VaultNotificationDao {
    @Query("SELECT * FROM vault_notifications WHERE vaultScope = :vaultScope ORDER BY postedAt DESC")
    List<VaultNotificationEntity> getAll(int vaultScope);

    @Query("SELECT * FROM vault_notifications WHERE vaultScope = :vaultScope AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%') ORDER BY postedAt DESC")
    List<VaultNotificationEntity> search(int vaultScope, String query);

    @Query("SELECT * FROM vault_notifications WHERE vaultScope = :vaultScope AND packageName = :packageName ORDER BY postedAt DESC")
    List<VaultNotificationEntity> getByPackage(int vaultScope, String packageName);

    @Query("SELECT COUNT(*) FROM vault_notifications WHERE vaultScope = :vaultScope")
    int count(int vaultScope);

    @Query("SELECT COUNT(*) FROM vault_notifications WHERE vaultScope = :vaultScope AND read = 0")
    int unreadCount(int vaultScope);

    @Query("SELECT packageName, COUNT(*) as cnt FROM vault_notifications WHERE vaultScope = :vaultScope AND read = 0 GROUP BY packageName")
    List<PackageUnreadCount> unreadByPackage(int vaultScope);

    @Insert
    long insert(VaultNotificationEntity entity);

    @Query("UPDATE vault_notifications SET read = 1 WHERE id = :id AND vaultScope = :vaultScope")
    void markRead(long id, int vaultScope);

    @Query("DELETE FROM vault_notifications WHERE id = :id AND vaultScope = :vaultScope")
    void delete(long id, int vaultScope);

    @Query("DELETE FROM vault_notifications WHERE id NOT IN (SELECT id FROM vault_notifications ORDER BY postedAt DESC LIMIT :maxRows)")
    void trimToMax(int maxRows);

    class PackageUnreadCount {
        public String packageName;
        public int cnt;
    }
}
