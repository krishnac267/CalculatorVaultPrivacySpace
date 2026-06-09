package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;

import javax.inject.Inject;

public final class ToggleNoteFavoriteUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public ToggleNoteFavoriteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void execute(long id) {
        noteRepository.toggleFavorite(id);
    }
}
