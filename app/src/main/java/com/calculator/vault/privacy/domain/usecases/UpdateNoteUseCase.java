package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.model.SecureNote;

import javax.inject.Inject;

public final class UpdateNoteUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public UpdateNoteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public SecureNote execute(long id, String title, String content) {
        return noteRepository.updateNote(id, title, content);
    }
}
