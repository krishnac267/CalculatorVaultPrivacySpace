package com.calculator.vault.privacy.data.repositories;

import com.calculator.vault.privacy.core.security.PinManager;
import com.calculator.vault.privacy.data.database.dao.IntruderLogDao;
import com.calculator.vault.privacy.data.database.entity.IntruderLogEntity;
import com.calculator.vault.privacy.domain.interfaces.IntruderRepository;
import com.calculator.vault.privacy.domain.model.IntruderLog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class IntruderRepositoryImpl implements IntruderRepository {
    private static final int LOG_THRESHOLD = 3;

    private final IntruderLogDao intruderLogDao;
    private final PinManager pinManager;

    @Inject
    public IntruderRepositoryImpl(IntruderLogDao intruderLogDao, PinManager pinManager) {
        this.intruderLogDao = intruderLogDao;
        this.pinManager = pinManager;
    }

    @Override
    public List<IntruderLog> getRecentLogs(int limit) {
        List<IntruderLogEntity> entities = intruderLogDao.getRecent(limit);
        List<IntruderLog> logs = new ArrayList<>(entities.size());
        for (IntruderLogEntity entity : entities) {
            logs.add(new IntruderLog(
                    entity.id,
                    entity.timestamp,
                    entity.attemptCount,
                    entity.detail,
                    entity.photoPath
            ));
        }
        return logs;
    }

    @Override
    public void logFailedAttempt(int attemptCount, String detail, String photoPath) {
        if (attemptCount < LOG_THRESHOLD) return;
        IntruderLogEntity entity = new IntruderLogEntity();
        entity.timestamp = System.currentTimeMillis();
        entity.attemptCount = attemptCount;
        entity.detail = detail == null ? "Invalid PIN attempt" : detail;
        entity.photoPath = photoPath;
        intruderLogDao.insert(entity);
        pinManager.resetFailedAttempts();
    }

    @Override
    public int getLogCount() {
        return intruderLogDao.count();
    }

    @Override
    public void deleteLog(long id) {
        intruderLogDao.delete(id);
    }
}
