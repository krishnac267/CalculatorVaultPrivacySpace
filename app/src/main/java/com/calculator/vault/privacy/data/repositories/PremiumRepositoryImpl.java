package com.calculator.vault.privacy.data.repositories;



import com.calculator.vault.privacy.core.FeatureFlags;

import com.calculator.vault.privacy.core.security.PinManager;

import com.calculator.vault.privacy.domain.interfaces.PremiumRepository;

import com.calculator.vault.privacy.domain.model.PremiumStatus;



import javax.inject.Inject;

import javax.inject.Singleton;



@Singleton

public final class PremiumRepositoryImpl implements PremiumRepository {

    private static final String KEY_PREMIUM = "premium_enabled";



    private final PinManager pinManager;



    @Inject

    public PremiumRepositoryImpl(PinManager pinManager) {

        this.pinManager = pinManager;

    }



    @Override

    public PremiumStatus getStatus() {

        if (!FeatureFlags.PREMIUM_ENABLED) {

            return PremiumStatus.unlimitedFreeTier();

        }

        return pinManager.getBoolean(KEY_PREMIUM, false)

                ? PremiumStatus.premiumTier()

                : PremiumStatus.freeTier();

    }



    @Override

    public void setPremiumForTesting(boolean premium) {

        if (!FeatureFlags.PREMIUM_ENABLED) {

            return;

        }

        pinManager.putBoolean(KEY_PREMIUM, premium);

    }

}

