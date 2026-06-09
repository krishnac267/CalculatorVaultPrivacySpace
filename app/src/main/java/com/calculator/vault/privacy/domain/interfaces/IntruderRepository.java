package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.IntruderLog;

import java.util.List;

public interface IntruderRepository {
    List<IntruderLog> getRecentLogs(int limit);
    void logFailedAttempt(int attemptCount, String detail, String photoPath);
    int getLogCount();
    void deleteLog(long id);
}
