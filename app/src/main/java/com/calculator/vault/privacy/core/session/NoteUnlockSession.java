package com.calculator.vault.privacy.core.session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/** Tracks per-note unlock state within an active vault session. */
@Singleton
public final class NoteUnlockSession {
    private final Set<Long> unlockedNoteIds = ConcurrentHashMap.newKeySet();

    @Inject
    public NoteUnlockSession() {}

    public boolean isUnlocked(long noteId) {
        return unlockedNoteIds.contains(noteId);
    }

    public void unlock(long noteId) {
        unlockedNoteIds.add(noteId);
    }

    public void lock(long noteId) {
        unlockedNoteIds.remove(noteId);
    }

    public void lockAll() {
        unlockedNoteIds.clear();
    }
}
