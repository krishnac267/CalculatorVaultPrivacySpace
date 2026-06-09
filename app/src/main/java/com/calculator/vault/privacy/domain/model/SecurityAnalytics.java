package com.calculator.vault.privacy.domain.model;

public final class SecurityAnalytics {
    private final int failedPinAttempts;
    private final int intruderEventCount;
    private final boolean biometricEnabled;
    private final boolean intruderCaptureEnabled;
    private final boolean rooted;
    private final boolean emulator;
    private final int sessionTimeoutMinutes;
    private final long lastLoginAt;
    private final SessionState sessionState;
    private final int securityScore;

    public SecurityAnalytics(
            int failedPinAttempts,
            int intruderEventCount,
            boolean biometricEnabled,
            boolean intruderCaptureEnabled,
            boolean rooted,
            boolean emulator,
            int sessionTimeoutMinutes,
            long lastLoginAt,
            SessionState sessionState,
            int securityScore
    ) {
        this.failedPinAttempts = failedPinAttempts;
        this.intruderEventCount = intruderEventCount;
        this.biometricEnabled = biometricEnabled;
        this.intruderCaptureEnabled = intruderCaptureEnabled;
        this.rooted = rooted;
        this.emulator = emulator;
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
        this.lastLoginAt = lastLoginAt;
        this.sessionState = sessionState;
        this.securityScore = securityScore;
    }

    public int getFailedPinAttempts() { return failedPinAttempts; }
    public int getIntruderEventCount() { return intruderEventCount; }
    public boolean isBiometricEnabled() { return biometricEnabled; }
    public boolean isIntruderCaptureEnabled() { return intruderCaptureEnabled; }
    public boolean isRooted() { return rooted; }
    public boolean isEmulator() { return emulator; }
    public int getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public long getLastLoginAt() { return lastLoginAt; }
    public SessionState getSessionState() { return sessionState; }
    public int getSecurityScore() { return securityScore; }
}
