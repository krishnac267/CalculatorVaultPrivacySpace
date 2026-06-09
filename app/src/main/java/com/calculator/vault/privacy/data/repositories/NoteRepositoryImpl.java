package com.calculator.vault.privacy.data.repositories;



import com.calculator.vault.privacy.core.security.ContentEncryptionService;

import com.calculator.vault.privacy.core.security.PinManager;

import com.calculator.vault.privacy.core.session.NoteUnlockSession;

import com.calculator.vault.privacy.core.session.SessionManager;

import com.calculator.vault.privacy.data.database.dao.SecureNoteDao;

import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity;

import com.calculator.vault.privacy.data.datasource.EntityMapper;

import com.calculator.vault.privacy.data.migration.NoteEncryptionMigrator;

import com.calculator.vault.privacy.domain.interfaces.NoteRepository;

import com.calculator.vault.privacy.domain.model.LockedNoteException;

import com.calculator.vault.privacy.domain.model.SecureNote;



import java.util.ArrayList;

import java.util.List;



import javax.inject.Inject;

import javax.inject.Singleton;



@Singleton

public final class NoteRepositoryImpl implements NoteRepository {

    private final SecureNoteDao secureNoteDao;

    private final SessionManager sessionManager;

    private final ContentEncryptionService encryptionService;

    private final NoteUnlockSession noteUnlockSession;

    private final PinManager pinManager;



    @Inject

    public NoteRepositoryImpl(

            SecureNoteDao secureNoteDao,

            SessionManager sessionManager,

            ContentEncryptionService encryptionService,

            NoteUnlockSession noteUnlockSession,

            PinManager pinManager

    ) {

        this.secureNoteDao = secureNoteDao;

        this.sessionManager = sessionManager;

        this.encryptionService = encryptionService;

        this.noteUnlockSession = noteUnlockSession;

        this.pinManager = pinManager;

    }



    private int scope() {

        return sessionManager.getCurrentVaultScope();

    }



    @Override

    public List<SecureNote> getAllNotes() {

        List<SecureNote> notes = new ArrayList<>();

        for (SecureNoteEntity entity : secureNoteDao.getAll(scope())) {

            notes.add(toSummary(entity));

        }

        return notes;

    }



    @Override

    public List<SecureNote> searchNotes(String query) {

        if (query == null || query.isBlank()) return getAllNotes();

        List<SecureNote> notes = new ArrayList<>();

        for (SecureNoteEntity entity : secureNoteDao.search(scope(), query.trim())) {

            notes.add(toSummary(entity));

        }

        return notes;

    }



    @Override

    public SecureNote getNote(long id) {

        SecureNoteEntity entity = requireEntity(id);

        if (entity.locked && !noteUnlockSession.isUnlocked(id)) {

            return toLockedSummary(entity);

        }

        return toDetail(entity, decryptContent(entity));

    }



    @Override

    public SecureNote unlockNote(long id, String pin) {

        boolean isFake = sessionManager.getSessionState() == com.calculator.vault.privacy.domain.model.SessionState.FAKE_VAULT;
        if (!pinManager.verifyPin(pin, isFake)) {

            throw new IllegalArgumentException("Invalid PIN");

        }

        SecureNoteEntity entity = requireEntity(id);

        noteUnlockSession.unlock(id);

        return toDetail(entity, decryptContent(entity));

    }



    @Override

    public SecureNote createNote(String title, String content) {

        return createNote(title, content, false);

    }



    @Override

    public SecureNote createNote(String title, String content, boolean locked) {

        long now = System.currentTimeMillis();

        String normalizedTitle = title == null || title.isBlank() ? "Untitled" : title.trim();

        String normalizedContent = content == null ? "" : content;

        SecureNoteEntity entity = new SecureNoteEntity();

        entity.title = normalizedTitle;

        entity.content = "";

        entity.encryptedContent = encryptionService.encryptText(normalizedContent);

        entity.searchText = NoteEncryptionMigrator.buildSearchText(normalizedTitle, normalizedContent);

        entity.favorite = false;

        entity.locked = locked;

        entity.vaultScope = scope();

        entity.createdAt = now;

        entity.updatedAt = now;

        entity.id = secureNoteDao.insert(entity);

        return toDetail(entity, normalizedContent);

    }



    @Override

    public SecureNote updateNote(long id, String title, String content) {

        SecureNoteEntity entity = requireEntity(id);

        if (entity.locked && !noteUnlockSession.isUnlocked(id)) {

            throw new LockedNoteException(id);

        }

        String normalizedTitle = title == null || title.isBlank() ? "Untitled" : title.trim();

        String normalizedContent = content == null ? "" : content;

        entity.title = normalizedTitle;

        entity.content = "";

        entity.encryptedContent = encryptionService.encryptText(normalizedContent);

        entity.searchText = NoteEncryptionMigrator.buildSearchText(normalizedTitle, normalizedContent);

        entity.updatedAt = System.currentTimeMillis();

        secureNoteDao.update(entity);

        return toDetail(entity, normalizedContent);

    }



    @Override

    public void deleteNote(long id) {

        noteUnlockSession.lock(id);

        secureNoteDao.delete(id, scope());

    }



    @Override

    public void toggleFavorite(long id) {

        secureNoteDao.toggleFavorite(id, scope());

    }



    @Override

    public void toggleLocked(long id) {

        secureNoteDao.toggleLocked(id, scope());

        noteUnlockSession.lock(id);

    }



    @Override

    public int getNoteCount() {

        return secureNoteDao.count(scope());

    }



    private SecureNoteEntity requireEntity(long id) {

        SecureNoteEntity entity = secureNoteDao.findById(id, scope());

        if (entity == null) throw new IllegalArgumentException("Note not found");

        return entity;

    }



    private String decryptContent(SecureNoteEntity entity) {

        if (entity.encryptedContent != null && !entity.encryptedContent.isEmpty()) {

            return encryptionService.decryptText(entity.encryptedContent);

        }

        return entity.content == null ? "" : entity.content;

    }



    private SecureNote toSummary(SecureNoteEntity entity) {

        return new SecureNote(

                entity.id,

                entity.title,

                "",

                entity.favorite,

                entity.locked,

                true,

                entity.createdAt,

                entity.updatedAt

        );

    }



    private SecureNote toLockedSummary(SecureNoteEntity entity) {

        return new SecureNote(

                entity.id,

                entity.title,

                "",

                entity.favorite,

                true,

                true,

                entity.createdAt,

                entity.updatedAt

        );

    }



    private SecureNote toDetail(SecureNoteEntity entity, String content) {

        return new SecureNote(

                entity.id,

                entity.title,

                content,

                entity.favorite,

                entity.locked,

                false,

                entity.createdAt,

                entity.updatedAt

        );

    }

}

