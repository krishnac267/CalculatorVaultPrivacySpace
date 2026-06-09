package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;
import com.calculator.vault.privacy.domain.model.PinValidationResult;
import com.calculator.vault.privacy.domain.model.SessionState;

import javax.inject.Inject;

public final class UnlockSessionUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public UnlockSessionUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public void execute(PinValidationResult result) {
        SessionState state = result == PinValidationResult.FAKE_VAULT
                ? SessionState.FAKE_VAULT
                : SessionState.REAL_VAULT;
        securityRepository.unlockSession(state);
    }
}
