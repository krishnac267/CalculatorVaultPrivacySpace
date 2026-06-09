package com.calculator.vault.privacy.domain.model;

public final class CloneSpaceStatus {
    private final boolean ready;
    private final String message;

    public CloneSpaceStatus(boolean ready, String message) {
        this.ready = ready;
        this.message = message;
    }

    public boolean isReady() {
        return ready;
    }

    public String getMessage() {
        return message;
    }
}
