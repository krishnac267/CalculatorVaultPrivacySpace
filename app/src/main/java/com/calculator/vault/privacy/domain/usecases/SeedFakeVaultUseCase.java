package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.core.session.FakeVaultSeeder;
import com.calculator.vault.privacy.domain.model.PinValidationResult;
import com.calculator.vault.privacy.domain.model.SessionState;

import javax.inject.Inject;

public final class SeedFakeVaultUseCase {
    private final FakeVaultSeeder fakeVaultSeeder;
    private final UnlockSessionUseCase unlockSessionUseCase;

    @Inject
    public SeedFakeVaultUseCase(FakeVaultSeeder fakeVaultSeeder, UnlockSessionUseCase unlockSessionUseCase) {
        this.fakeVaultSeeder = fakeVaultSeeder;
        this.unlockSessionUseCase = unlockSessionUseCase;
    }

    public void execute(PinValidationResult result) {
        unlockSessionUseCase.execute(result);
        if (result == PinValidationResult.FAKE_VAULT) {
            fakeVaultSeeder.seedIfEmpty();
        }
    }
}
