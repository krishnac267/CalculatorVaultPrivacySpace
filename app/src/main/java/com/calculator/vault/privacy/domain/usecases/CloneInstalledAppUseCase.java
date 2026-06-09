package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.core.clone.WorkProfileCloneManager;
import com.calculator.vault.privacy.domain.model.InstalledApp;

import javax.inject.Inject;

public final class CloneInstalledAppUseCase {
    private final WorkProfileCloneManager workProfileCloneManager;
    private final RecordAppLaunchUseCase recordAppLaunchUseCase;

    @Inject
    public CloneInstalledAppUseCase(
            WorkProfileCloneManager workProfileCloneManager,
            RecordAppLaunchUseCase recordAppLaunchUseCase
    ) {
        this.workProfileCloneManager = workProfileCloneManager;
        this.recordAppLaunchUseCase = recordAppLaunchUseCase;
    }

    public void onInstallSucceeded(InstalledApp app) {
        recordAppLaunchUseCase.execute(
                app.getPackageName(),
                app.getLabel() + " (Clone)",
                app.getCategory(),
                true
        );
    }

    public boolean isAlreadyCloned(String packageName) {
        return workProfileCloneManager.isCloned(packageName);
    }
}
