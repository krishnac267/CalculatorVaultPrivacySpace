package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.core.clone.WorkProfileCloneManager;
import com.calculator.vault.privacy.domain.model.CloneSpaceStatus;

import javax.inject.Inject;

public final class GetCloneSpaceStatusUseCase {
    private final WorkProfileCloneManager workProfileCloneManager;

    @Inject
    public GetCloneSpaceStatusUseCase(WorkProfileCloneManager workProfileCloneManager) {
        this.workProfileCloneManager = workProfileCloneManager;
    }

    public CloneSpaceStatus execute() {
        return workProfileCloneManager.getStatus();
    }
}
