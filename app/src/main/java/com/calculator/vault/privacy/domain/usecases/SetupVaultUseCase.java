package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;
import com.calculator.vault.privacy.domain.validators.PinValidator;

import javax.inject.Inject;

public final class SetupVaultUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public SetupVaultUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public void execute(String pin, String fakePin, boolean biometricEnabled) {
        if (!PinValidator.isValid(pin)) {
            throw new IllegalArgumentException("PIN must be 4-8 digits");
        }
        securityRepository.setupVault(pin, fakePin, biometricEnabled);
    }
}
