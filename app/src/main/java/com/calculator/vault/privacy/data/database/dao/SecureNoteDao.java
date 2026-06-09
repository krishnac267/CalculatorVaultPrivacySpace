package com.calculator.vault.privacy.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;

import java.util.List;

@Dao
public interface SecureNoteDao {
    @Query("SELECT * FROM secure_notes WHERE vaultScope = :vaultScope ORDER BY updatedAt DESC")
    List<SecureNoteEntity> getAll(int vaultScope);

    @Query("SELECT * FROM secure_notes WHERE vaultScope = :vaultScope AND (title LIKE '%' || :query || '%' OR searchText LIKE '%' || :query || '%') ORDER BY updatedAt DESC")
    List<SecureNoteEntity> search(int vaultScope, String query);

    @Query("SELECT * FROM secure_notes WHERE vaultScope = :vaultScope ORDER BY updatedAt DESC LIMIT :limit")
    List<SecureNoteEntity> getRecent(int vaultScope, int limit);

    @Query("SELECT COUNT(*) FROM secure_notes WHERE vaultScope = :vaultScope")
    int count(int vaultScope);

    @Query("SELECT * FROM secure_notes WHERE id = :id AND vaultScope = :vaultScope LIMIT 1")
    SecureNoteEntity findById(long id, int vaultScope);

    @Query("SELECT * FROM secure_notes WHERE encryptedContent IS NULL OR encryptedContent = ''")
    List<SecureNoteEntity> getNotesNeedingEncryption();

    @Query("SELECT * FROM secure_notes WHERE encryptedContent IS NOT NULL AND encryptedContent != ''")
    List<SecureNoteEntity> getAllEncryptedNotes();

    @Insert
    long insert(SecureNoteEntity entity);

    @Update
    void update(SecureNoteEntity entity);

    @Query("DELETE FROM secure_notes WHERE id = :id AND vaultScope = :vaultScope")
    void delete(long id, int vaultScope);

    @Query("UPDATE secure_notes SET favorite = CASE favorite WHEN 1 THEN 0 ELSE 1 END WHERE id = :id AND vaultScope = :vaultScope")
    void toggleFavorite(long id, int vaultScope);

    @Query("UPDATE secure_notes SET locked = CASE locked WHEN 1 THEN 0 ELSE 1 END WHERE id = :id AND vaultScope = :vaultScope")
    void toggleLocked(long id, int vaultScope);
}
