package com.calculator.vault.privacy.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.calculator.vault.privacy.data.database.entity.IntruderLogEntity;

import java.util.List;

@Dao
public interface IntruderLogDao {
    @Query("SELECT * FROM intruder_logs ORDER BY timestamp DESC LIMIT :limit")
    List<IntruderLogEntity> getRecent(int limit);

    @Query("SELECT COUNT(*) FROM intruder_logs")
    int count();

    @Insert
    long insert(IntruderLogEntity entity);

    @Query("DELETE FROM intruder_logs WHERE id = :id")
    void delete(long id);
}
