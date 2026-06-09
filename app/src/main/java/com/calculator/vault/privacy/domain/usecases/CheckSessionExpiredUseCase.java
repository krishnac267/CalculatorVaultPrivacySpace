package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import javax.inject.Inject;

public final class CheckSessionExpiredUseCase {
    private final SecurityRepository securityRepository;

    @Inject
    public CheckSessionExpiredUseCase(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    public boolean execute() {
        return securityRepository.isSessionExpired();
    }
}
