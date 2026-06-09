package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import javax.inject.Inject;

public final class LockSessionUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public LockSessionUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public void execute() {
        securityRepository.lockSession();
    }
}
