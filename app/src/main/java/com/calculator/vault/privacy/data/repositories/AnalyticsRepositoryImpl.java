package com.calculator.vault.privacy.data.repositories;

import com.calculator.vault.privacy.core.security.DeviceSecurityChecker;
import com.calculator.vault.privacy.core.security.PinManager;
import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.domain.interfaces.AnalyticsRepository;
import com.calculator.vault.privacy.domain.interfaces.AppRepository;
import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.interfaces.IntruderRepository;
import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.interfaces.NotificationRepository;
import com.calculator.vault.privacy.domain.interfaces.PremiumRepository;
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;
import com.calculator.vault.privacy.domain.model.PremiumStatus;
import com.calculator.vault.privacy.domain.model.SecurityAnalytics;
import com.calculator.vault.privacy.domain.model.SessionState;
import com.calculator.vault.privacy.domain.model.StorageAnalytics;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AnalyticsRepositoryImpl implements AnalyticsRepository {
    private final SecurityRepository securityRepository;
    private final IntruderRepository intruderRepository;
    private final DeviceSecurityChecker deviceSecurityChecker;
    private final PinManager pinManager;
    private final SessionManager sessionManager;
    private final FileRepository fileRepository;
    private final NoteRepository noteRepository;
    private final AppRepository appRepository;
    private final NotificationRepository notificationRepository;
    private final PremiumRepository premiumRepository;

    @Inject
    public AnalyticsRepositoryImpl(
            SecurityRepository securityRepository,
            IntruderRepository intruderRepository,
            DeviceSecurityChecker deviceSecurityChecker,
            PinManager pinManager,
            SessionManager sessionManager,
            FileRepository fileRepository,
            NoteRepository noteRepository,
            AppRepository appRepository,
            NotificationRepository notificationRepository,
            PremiumRepository premiumRepository
    ) {
        this.securityRepository = securityRepository;
        this.intruderRepository = intruderRepository;
        this.deviceSecurityChecker = deviceSecurityChecker;
        this.pinManager = pinManager;
        this.sessionManager = sessionManager;
        this.fileRepository = fileRepository;
        this.noteRepository = noteRepository;
        this.appRepository = appRepository;
        this.notificationRepository = notificationRepository;
        this.premiumRepository = premiumRepository;
    }

    @Override
    public SecurityAnalytics loadSecurityAnalytics() {
        SessionState state = securityRepository.getSessionState();
        boolean rooted = deviceSecurityChecker.isRooted();
        boolean emulator = deviceSecurityChecker.isEmulator();
        int score = 100;
        if (!securityRepository.isBiometricEnabled()) score -= 10;
        if (!pinManager.isIntruderCaptureEnabled()) score -= 5;
        if (rooted) score -= 25;
        if (emulator) score -= 15;
        if (securityRepository.getSessionTimeoutMinutes() > 15) score -= 10;
        if (score < 0) score = 0;

        return new SecurityAnalytics(
                securityRepository.getFailedPinAttempts(),
                intruderRepository.getLogCount(),
                securityRepository.isBiometricEnabled(),
                pinManager.isIntruderCaptureEnabled(),
                rooted,
                emulator,
                securityRepository.getSessionTimeoutMinutes(),
                securityRepository.getLastLoginAt(),
                state,
                score
        );
    }

    @Override
    public StorageAnalytics loadStorageAnalytics() {
        PremiumStatus status = premiumRepository.getStatus();
        long used = fileRepository.getTotalStorageBytes();
        long limit = status.getStorageLimitBytes();
        int usagePercent = limit <= 0L || limit >= Long.MAX_VALUE / 2
                ? 0
                : (int) Math.min(100, (used * 100) / limit);
        return new StorageAnalytics(
                used,
                limit,
                noteRepository.getNoteCount(),
                status.getNoteLimit(),
                appRepository.getAppCount(),
                status.getVaultAppLimit(),
                fileRepository.getFileCount(),
                notificationRepository.getNotificationCount(),
                status.isPremium(),
                usagePercent
        );
    }
}
