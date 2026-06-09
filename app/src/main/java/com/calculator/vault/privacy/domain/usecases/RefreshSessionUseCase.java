package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import javax.inject.Inject;

public final class RefreshSessionUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public RefreshSessionUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public void execute() {
        if (securityRepository.getSessionState() != com.calculator.vault.privacy.domain.model.SessionState.LOCKED) {
            securityRepository.refreshSession();
        }
    }
}
