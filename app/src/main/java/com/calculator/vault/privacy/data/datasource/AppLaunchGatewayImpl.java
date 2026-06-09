package com.calculator.vault.privacy.data.datasource;

import com.calculator.vault.privacy.domain.interfaces.AppLaunchGateway;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AppLaunchGatewayImpl implements AppLaunchGateway {
    private final AppLauncher appLauncher;

    @Inject
    public AppLaunchGatewayImpl(AppLauncher appLauncher) {
        this.appLauncher = appLauncher;
    }

    @Override
    public void launch(String packageName) {
        appLauncher.launch(packageName);
    }
}
