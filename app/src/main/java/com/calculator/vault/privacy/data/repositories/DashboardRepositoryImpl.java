package com.calculator.vault.privacy.data.repositories;

import com.calculator.vault.privacy.core.session.SessionManager;
import com.calculator.vault.privacy.domain.interfaces.AppRepository;
import com.calculator.vault.privacy.domain.interfaces.DashboardRepository;
import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.interfaces.NotificationRepository;
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;
import com.calculator.vault.privacy.domain.model.DashboardSummary;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class DashboardRepositoryImpl implements DashboardRepository {
    private final SecurityRepository securityRepository;
    private final AppRepository appRepository;
    private final NoteRepository noteRepository;
    private final FileRepository fileRepository;
    private final NotificationRepository notificationRepository;
    private final SessionManager sessionManager;

    @Inject
    public DashboardRepositoryImpl(
            SecurityRepository securityRepository,
            AppRepository appRepository,
            NoteRepository noteRepository,
            FileRepository fileRepository,
            NotificationRepository notificationRepository,
            SessionManager sessionManager
    ) {
        this.securityRepository = securityRepository;
        this.appRepository = appRepository;
        this.noteRepository = noteRepository;
        this.fileRepository = fileRepository;
        this.notificationRepository = notificationRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public DashboardSummary loadSummary() {
        List<com.calculator.vault.privacy.domain.model.SecureNote> notes = noteRepository.getAllNotes();
        int recentCount = Math.min(3, notes.size());
        return new DashboardSummary(
                securityRepository.getSessionState(),
                securityRepository.getLastLoginAt(),
                noteRepository.getNoteCount(),
                fileRepository.getFileCount(),
                notificationRepository.getNotificationCount(),
                appRepository.getAppCount(),
                fileRepository.getTotalStorageBytes(),
                appRepository.getRecentApps(5),
                appRepository.getFavoriteApps(),
                notes.subList(0, recentCount)
        );
    }
}
