package com.calculator.vault.privacy.domain.model;

public final class LockedNoteException extends RuntimeException {
    private final long noteId;

    public LockedNoteException(long noteId) {
        super("Note requires PIN re-authentication");
        this.noteId = noteId;
    }

    public long getNoteId() {
        return noteId;
    }
}
