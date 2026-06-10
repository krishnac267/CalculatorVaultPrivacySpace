package com.calculator.vault.privacy.domain.model;

public final class CloneSpaceStatus {
    private final boolean ready;
    private final boolean canEnable;
    private final String message;

    public CloneSpaceStatus(boolean ready, boolean canEnable, String message) {
        this.ready = ready;
        this.canEnable = canEnable;
        this.message = message;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isSetupAvailable() {
        return canEnable;
    }

    public String getMessage() {
        return message;
    }
}
