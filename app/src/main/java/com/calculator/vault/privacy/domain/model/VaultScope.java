package com.calculator.vault.privacy.domain.model;

public final class VaultScope {
    public static final int REAL = 0;
    public static final int FAKE = 1;

    private VaultScope() {}

    public static int fromSession(SessionState state) {
        return state == SessionState.FAKE_VAULT ? FAKE : REAL;
    }
}
