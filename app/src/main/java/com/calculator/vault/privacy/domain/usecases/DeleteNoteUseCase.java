package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;

import javax.inject.Inject;

public final class DeleteNoteUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public DeleteNoteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void execute(long id) {
        noteRepository.deleteNote(id);
    }
}
