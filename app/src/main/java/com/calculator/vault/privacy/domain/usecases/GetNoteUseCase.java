package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.model.SecureNote;

import javax.inject.Inject;

public final class GetNoteUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public GetNoteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public SecureNote execute(long id) {
        return noteRepository.getNote(id);
    }
}
