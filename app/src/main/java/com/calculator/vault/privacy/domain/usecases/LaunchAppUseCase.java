package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.AppLaunchGateway;
import com.calculator.vault.privacy.domain.model.InstalledApp;
import com.calculator.vault.privacy.domain.model.VaultApp;

import javax.inject.Inject;

public final class LaunchAppUseCase {
    private final AppLaunchGateway appLaunchGateway;
    private final RecordAppLaunchUseCase recordAppLaunchUseCase;

    @Inject
    public LaunchAppUseCase(AppLaunchGateway appLaunchGateway, RecordAppLaunchUseCase recordAppLaunchUseCase) {
        this.appLaunchGateway = appLaunchGateway;
        this.recordAppLaunchUseCase = recordAppLaunchUseCase;
    }

    public void execute(InstalledApp app) {
        recordAppLaunchUseCase.execute(app.getPackageName(), app.getLabel(), app.getCategory(), false);
        appLaunchGateway.launch(app.getPackageName());
    }

    public void executeVaultApp(VaultApp app) {
        recordAppLaunchUseCase.execute(
                app.getPackageName(),
                app.getLabel(),
                app.getCategory(),
                app.isClone()
        );
        if (app.isClone()) {
            appLaunchGateway.launchClone(app.getPackageName());
        } else {
            appLaunchGateway.launch(app.getPackageName());
        }
    }
}
