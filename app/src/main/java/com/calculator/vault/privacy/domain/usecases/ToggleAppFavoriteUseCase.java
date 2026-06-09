package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.AppRepository;

import javax.inject.Inject;

public final class ToggleAppFavoriteUseCase {
    private final AppRepository appRepository;

    @Inject
    public ToggleAppFavoriteUseCase(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public void execute(long id) {
        appRepository.toggleFavorite(id);
    }
}
