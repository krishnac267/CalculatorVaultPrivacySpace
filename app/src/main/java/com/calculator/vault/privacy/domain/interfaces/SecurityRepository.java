package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.PinValidationResult;
import com.calculator.vault.privacy.domain.model.SessionState;

public interface SecurityRepository {
    boolean isSetupComplete();
    PinValidationResult validatePin(String pin);
    void setupVault(String pin, String fakePin, boolean biometricEnabled);
    void panicLogout();
    SessionState getSessionState();
    void lockSession();
    void unlockSession(SessionState state);
    void unlockWithBiometric();
    boolean isSessionExpired();
    void refreshSession();
    void setSessionTimeoutMinutes(int minutes);
    int getSessionTimeoutMinutes();
    boolean isBiometricEnabled();
    void setBiometricEnabled(boolean enabled);
    long getLastLoginAt();
    int recordFailedPinAttempt();
    void resetFailedPinAttempts();
    int getFailedPinAttempts();
    boolean isIntruderCaptureEnabled();
    void setIntruderCaptureEnabled(boolean enabled);
}
