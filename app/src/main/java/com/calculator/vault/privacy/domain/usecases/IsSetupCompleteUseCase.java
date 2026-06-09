package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import javax.inject.Inject;

public final class IsSetupCompleteUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public IsSetupCompleteUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public boolean execute() {
        return securityRepository.isSetupComplete();
    }
}
