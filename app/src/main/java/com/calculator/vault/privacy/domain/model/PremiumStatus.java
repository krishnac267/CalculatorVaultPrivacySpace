package com.calculator.vault.privacy.domain.model;

public final class PremiumStatus {
    private final boolean premium;
    private final int vaultAppLimit;
    private final int noteLimit;
    private final long storageLimitBytes;

    public PremiumStatus(boolean premium, int vaultAppLimit, int noteLimit, long storageLimitBytes) {
        this.premium = premium;
        this.vaultAppLimit = vaultAppLimit;
        this.noteLimit = noteLimit;
        this.storageLimitBytes = storageLimitBytes;
    }

    public boolean isPremium() { return premium; }
    public int getVaultAppLimit() { return vaultAppLimit; }
    public int getNoteLimit() { return noteLimit; }
    public long getStorageLimitBytes() { return storageLimitBytes; }

    public static PremiumStatus freeTier() {
        return new PremiumStatus(false, 3, 10, 100L * 1024L * 1024L);
    }

    /** Full limits without premium upsell — used while billing is disabled. */
    public static PremiumStatus unlimitedFreeTier() {
        return new PremiumStatus(false, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
    }

    public static PremiumStatus premiumTier() {
        return new PremiumStatus(true, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
    }
}
