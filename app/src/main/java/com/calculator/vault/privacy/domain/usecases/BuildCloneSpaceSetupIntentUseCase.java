package com.calculator.vault.privacy.domain.usecases;

import android.content.Intent;

import com.calculator.vault.privacy.core.clone.WorkProfileCloneManager;

import javax.inject.Inject;

public final class BuildCloneSpaceSetupIntentUseCase {
    private final WorkProfileCloneManager workProfileCloneManager;

    @Inject
    public BuildCloneSpaceSetupIntentUseCase(WorkProfileCloneManager workProfileCloneManager) {
        this.workProfileCloneManager = workProfileCloneManager;
    }

    public Intent execute() {
        if (!workProfileCloneManager.canProvisionCloneSpace()) {
            throw new IllegalStateException("This device does not support Clone Space setup");
        }
        return workProfileCloneManager.buildProvisioningIntent();
    }
}
