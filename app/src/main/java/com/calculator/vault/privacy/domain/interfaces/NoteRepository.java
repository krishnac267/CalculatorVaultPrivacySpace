package com.calculator.vault.privacy.domain.interfaces;



import com.calculator.vault.privacy.domain.model.SecureNote;

import com.calculator.vault.privacy.domain.model.VaultFileCategory;



import java.util.List;



public interface NoteRepository {

    List<SecureNote> getAllNotes();

    List<SecureNote> searchNotes(String query);

    SecureNote getNote(long id);

    SecureNote unlockNote(long id, String pin);

    SecureNote createNote(String title, String content);

    SecureNote createNote(String title, String content, boolean locked);

    SecureNote updateNote(long id, String title, String content);

    void deleteNote(long id);

    void toggleFavorite(long id);

    void toggleLocked(long id);

    int getNoteCount();

}

