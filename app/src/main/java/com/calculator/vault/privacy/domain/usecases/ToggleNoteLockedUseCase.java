package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;

import javax.inject.Inject;

public final class ToggleNoteLockedUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public ToggleNoteLockedUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void execute(long id) {
        noteRepository.toggleLocked(id);
    }
}
