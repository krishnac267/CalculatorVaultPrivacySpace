package com.calculator.vault.privacy.domain.usecases;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.model.SecureNote;

import java.util.List;

import javax.inject.Inject;

public final class GetNotesUseCase {
    private final NoteRepository noteRepository;

    @Inject
    public GetNotesUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<SecureNote> execute(String query) {
        if (query == null || query.isBlank()) {
            return noteRepository.getAllNotes();
        }
        return noteRepository.searchNotes(query);
    }
}
