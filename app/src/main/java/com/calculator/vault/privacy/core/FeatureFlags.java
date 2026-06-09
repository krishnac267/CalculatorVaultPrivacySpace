package com.calculator.vault.privacy.core;

/** Feature toggles for staged rollout. */
public final class FeatureFlags {
    /** When false, premium upsell and quotas are hidden; all users get full limits. */
    public static final boolean PREMIUM_ENABLED = false;

    private FeatureFlags() {}
}
