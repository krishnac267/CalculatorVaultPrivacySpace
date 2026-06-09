package com.calculator.vault.privacy.data.datasource;

import com.calculator.vault.privacy.core.clone.WorkProfileCloneManager;
import com.calculator.vault.privacy.domain.interfaces.AppLaunchGateway;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AppLaunchGatewayImpl implements AppLaunchGateway {
    private final AppLauncher appLauncher;
    private final WorkProfileCloneManager workProfileCloneManager;

    @Inject
    public AppLaunchGatewayImpl(AppLauncher appLauncher, WorkProfileCloneManager workProfileCloneManager) {
        this.appLauncher = appLauncher;
        this.workProfileCloneManager = workProfileCloneManager;
    }

    @Override
    public void launch(String packageName) {
        appLauncher.launch(packageName);
    }

    @Override
    public void launchClone(String packageName) {
        workProfileCloneManager.launchClone(packageName);
    }
}
