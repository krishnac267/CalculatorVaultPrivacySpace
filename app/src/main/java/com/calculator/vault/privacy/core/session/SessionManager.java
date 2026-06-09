package com.calculator.vault.privacy.core.session;

import com.calculator.vault.privacy.core.security.PinManager;
import com.calculator.vault.privacy.data.storage.FileStorageManager;
import com.calculator.vault.privacy.domain.model.SessionState;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class SessionManager {
    public interface Listener {
        void onSessionChanged(SessionState state);
    }

    private static final int DEFAULT_TIMEOUT_MINUTES = 5;

    private final PinManager pinManager;
    private final NoteUnlockSession noteUnlockSession;
    private final FileStorageManager fileStorageManager;
    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();
    private volatile SessionState sessionState = SessionState.LOCKED;
    private volatile long lastActivityTime;

    @Inject
    public SessionManager(
            PinManager pinManager,
            NoteUnlockSession noteUnlockSession,
            FileStorageManager fileStorageManager
    ) {
        this.pinManager = pinManager;
        this.noteUnlockSession = noteUnlockSession;
        this.fileStorageManager = fileStorageManager;
    }

    public SessionState getSessionState() {
        return sessionState;
    }

    public boolean isUnlocked() {
        return sessionState != SessionState.LOCKED;
    }

    public void unlock(SessionState state) {
        sessionState = state;
        refresh();
        notifyListeners();
    }

    public void lock() {
        sessionState = SessionState.LOCKED;
        noteUnlockSession.lockAll();
        fileStorageManager.clearPreviewCache();
        notifyListeners();
    }

    public void panicLogout() {
        lock();
        pinManager.clearAll();
    }

    public void refresh() {
        lastActivityTime = System.currentTimeMillis();
        pinManager.putLong(PinManager.KEY_LAST_ACTIVITY_PREF, lastActivityTime);
    }

    public boolean isExpired() {
        if (sessionState == SessionState.LOCKED) return false;
        int timeoutMinutes = pinManager.getInt(
                PinManager.KEY_SESSION_TIMEOUT_PREF,
                DEFAULT_TIMEOUT_MINUTES
        );
        long lastActivity = pinManager.getLong(
                PinManager.KEY_LAST_ACTIVITY_PREF,
                lastActivityTime
        );
        long elapsed = System.currentTimeMillis() - lastActivity;
        return elapsed > timeoutMinutes * 60_000L;
    }

    public void setTimeoutMinutes(int minutes) {
        pinManager.putInt(PinManager.KEY_SESSION_TIMEOUT_PREF, minutes);
    }

    public int getTimeoutMinutes() {
        return pinManager.getInt(PinManager.KEY_SESSION_TIMEOUT_PREF, DEFAULT_TIMEOUT_MINUTES);
    }

    public int getCurrentVaultScope() {
        return com.calculator.vault.privacy.domain.model.VaultScope.fromSession(sessionState);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Listener listener : listeners) {
            listener.onSessionChanged(sessionState);
        }
    }
}
