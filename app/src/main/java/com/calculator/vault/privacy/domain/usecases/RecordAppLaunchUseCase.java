package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.AppRepository;

import javax.inject.Inject;

public final class RecordAppLaunchUseCase {
    private final AppRepository appRepository;

    @Inject
    public RecordAppLaunchUseCase(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public void execute(String packageName, String label, String category) {
        execute(packageName, label, category, false);
    }

    public void execute(String packageName, String label, String category, boolean clone) {
        appRepository.recordLaunch(packageName, label, category, clone);
    }
}
