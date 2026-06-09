package com.calculator.vault.privacy.domain.interfaces;

import com.calculator.vault.privacy.domain.model.PremiumStatus;

public interface PremiumRepository {
    PremiumStatus getStatus();
    void setPremiumForTesting(boolean premium);
}
