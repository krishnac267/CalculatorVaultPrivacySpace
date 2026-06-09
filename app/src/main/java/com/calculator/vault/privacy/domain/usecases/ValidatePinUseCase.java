package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;
import com.calculator.vault.privacy.domain.model.PinValidationResult;

import javax.inject.Inject;

public final class ValidatePinUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public ValidatePinUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public PinValidationResult execute(String pin) {
        return securityRepository.validatePin(pin);
    }
}
