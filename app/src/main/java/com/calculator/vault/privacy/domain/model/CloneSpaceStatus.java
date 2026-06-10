package com.calculator.vault.privacy.domain.model;

public final class CloneSpaceStatus {
    private final boolean ready;
    private final boolean canEnable;
    private final String message;
    private final CloneSpaceAlternative alternative;

    public CloneSpaceStatus(boolean ready, boolean canEnable, String message) {
        this(ready, canEnable, message, CloneSpaceAlternative.NONE);
    }

    public CloneSpaceStatus(
            boolean ready,
            boolean canEnable,
            String message,
            CloneSpaceAlternative alternative
    ) {
        this.ready = ready;
        this.canEnable = canEnable;
        this.message = message;
        this.alternative = alternative;
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

    public CloneSpaceAlternative getAlternative() {
        return alternative;
    }
}
