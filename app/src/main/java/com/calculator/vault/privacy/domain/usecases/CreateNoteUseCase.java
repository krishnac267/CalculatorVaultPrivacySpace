package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.model.SecureNote;

import javax.inject.Inject;

public final class CreateNoteUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public CreateNoteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public SecureNote execute(String title, String content) {
        return execute(title, content, false);
    }

    public SecureNote execute(String title, String content, boolean locked) {
        return noteRepository.createNote(title, content, locked);
    }
}
