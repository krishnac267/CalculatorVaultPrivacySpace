package com.calculator.vault.privacy.data.repositories;

import com.calculator.vault.privacy.core.security.PinManager;
import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;
import com.calculator.vault.privacy.domain.model.PinValidationResult;
import com.calculator.vault.privacy.domain.model.SessionState;
import com.calculator.vault.privacy.domain.validators.PinValidator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class SecurityRepositoryImpl implements SecurityRepository {
    private final PinManager pinManager;
    private final SessionManager sessionManager;

    @Inject
    public SecurityRepositoryImpl(PinManager pinManager, SessionManager sessionManager) {
        this.pinManager = pinManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean isSetupComplete() {
        return pinManager.isSetupComplete();
    }

    @Override
    public PinValidationResult validatePin(String pin) {
        if (!PinValidator.isValid(pin) || !pinManager.isSetupComplete()) {
            handleFailedAttempt();
            return PinValidationResult.INVALID;
        }
        if (pinManager.verifyPin(pin, false)) {
            resetFailedPinAttempts();
            return PinValidationResult.REAL_VAULT;
        }
        if (pinManager.isFakeVaultEnabled()
                && pinManager.hasFakePin()
                && pinManager.verifyPin(pin, true)) {
            resetFailedPinAttempts();
            return PinValidationResult.FAKE_VAULT;
        }
        handleFailedAttempt();
        return PinValidationResult.INVALID;
    }

    private void handleFailedAttempt() {
        recordFailedPinAttempt();
    }

    @Override
    public void setupVault(String pin, String fakePin, boolean biometricEnabled) {
        if (!PinValidator.isValid(pin)) {
            throw new IllegalArgumentException("PIN must be 4-8 digits");
        }
        if (fakePin != null && !fakePin.isBlank()) {
            if (!PinValidator.isValid(fakePin)) {
                throw new IllegalArgumentException("Fake PIN must be 4-8 digits");
            }
            if (!PinValidator.areDistinct(pin, fakePin)) {
                throw new IllegalArgumentException("Fake PIN must differ from real PIN");
            }
            pinManager.storePin(fakePin, true);
            pinManager.setFakeVaultEnabled(true);
        } else {
            pinManager.clearFakePin();
            pinManager.setFakeVaultEnabled(false);
        }
        pinManager.storePin(pin, false);
        pinManager.setBiometricEnabled(biometricEnabled);
        sessionManager.setTimeoutMinutes(5);
        pinManager.markSetupComplete();
        resetFailedPinAttempts();
    }

    @Override
    public void panicLogout() {
        sessionManager.panicLogout();
    }

    @Override
    public SessionState getSessionState() {
        return sessionManager.getSessionState();
    }

    @Override
    public void lockSession() {
        sessionManager.lock();
    }

    @Override
    public void unlockSession(SessionState state) {
        sessionManager.unlock(state);
        pinManager.setLastLoginAt(System.currentTimeMillis());
    }

    @Override
    public void unlockWithBiometric() {
        if (!pinManager.isBiometricEnabled()) {
            throw new IllegalStateException("Biometric unlock is disabled");
        }
        unlockSession(SessionState.REAL_VAULT);
    }

    @Override
    public boolean isSessionExpired() {
        return sessionManager.isExpired();
    }

    @Override
    public void refreshSession() {
        sessionManager.refresh();
    }

    @Override
    public void setSessionTimeoutMinutes(int minutes) {
        sessionManager.setTimeoutMinutes(minutes);
    }

    @Override
    public int getSessionTimeoutMinutes() {
        return sessionManager.getTimeoutMinutes();
    }

    @Override
    public boolean isBiometricEnabled() {
        return pinManager.isBiometricEnabled();
    }

    @Override
    public void setBiometricEnabled(boolean enabled) {
        pinManager.setBiometricEnabled(enabled);
    }

    @Override
    public long getLastLoginAt() {
        return pinManager.getLastLoginAt();
    }

    @Override
    public int recordFailedPinAttempt() {
        return pinManager.incrementFailedAttempts();
    }

    @Override
    public void resetFailedPinAttempts() {
        pinManager.resetFailedAttempts();
    }

    @Override
    public int getFailedPinAttempts() {
        return pinManager.getFailedAttempts();
    }

    @Override
    public boolean isIntruderCaptureEnabled() {
        return pinManager.isIntruderCaptureEnabled();
    }

    @Override
    public void setIntruderCaptureEnabled(boolean enabled) {
        pinManager.setIntruderCaptureEnabled(enabled);
    }
}
