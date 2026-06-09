package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import javax.inject.Inject;

public final class UnlockWithBiometricUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public UnlockWithBiometricUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public boolean execute() {
        if (!securityRepository.isBiometricEnabled()) return false;
        securityRepository.unlockWithBiometric();
        return true;
    }
}
