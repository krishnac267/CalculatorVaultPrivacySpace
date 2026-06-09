package com.calculator.vault.privacy.domain.interfaces;

public interface AppLaunchGateway {
    void launch(String packageName);

    void launchClone(String packageName);
}