package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.IntruderRepository;
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import javax.inject.Inject;

public final class HandleIntruderEventUseCase {
    private static final int THRESHOLD = 3;

    private final SecurityRepository securityRepository;
    private final IntruderRepository intruderRepository;

    @Inject
    public HandleIntruderEventUseCase(
            SecurityRepository securityRepository,
            IntruderRepository intruderRepository
    ) {
        this.securityRepository = securityRepository;
        this.intruderRepository = intruderRepository;
    }

    public boolean shouldLogIntruder() {
        return securityRepository.getFailedPinAttempts() >= THRESHOLD;
    }

    public boolean isCaptureEnabled() {
        return securityRepository.isIntruderCaptureEnabled();
    }

    public void logIntruder(String photoPath) {
        intruderRepository.logFailedAttempt(
                securityRepository.getFailedPinAttempts(),
                "Invalid calculator PIN entered",
                photoPath
        );
    }
}
