package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.model.SecureNote;

import javax.inject.Inject;

public final class UnlockNoteUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public UnlockNoteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public SecureNote execute(long id, String pin) {
        return noteRepository.unlockNote(id, pin);
    }
}
