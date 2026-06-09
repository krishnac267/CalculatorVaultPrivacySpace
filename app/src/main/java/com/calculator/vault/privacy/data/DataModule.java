package com.calculator.vault.privacy.data;

import android.content.Context;

import androidx.room.Room;

import com.calculator.vault.privacy.core.security.DatabaseKeyManager;
import com.calculator.vault.privacy.data.database.DatabaseMigrations;
import com.calculator.vault.privacy.data.database.PrivacySpaceDatabase;
import com.calculator.vault.privacy.data.database.dao.IntruderLogDao;
import com.calculator.vault.privacy.data.database.dao.SecureNoteDao;
import com.calculator.vault.privacy.data.database.dao.VaultAppDao;
import com.calculator.vault.privacy.data.database.dao.VaultFileDao;
import com.calculator.vault.privacy.data.database.dao.VaultNotificationDao;
import com.calculator.vault.privacy.data.datasource.AppLaunchGatewayImpl;
import com.calculator.vault.privacy.data.datasource.InstalledAppsRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.AnalyticsRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.AppRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.DashboardRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.FileRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.IntruderRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.NoteRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.NotificationRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.PremiumRepositoryImpl;
import com.calculator.vault.privacy.data.repositories.SecurityRepositoryImpl;
import com.calculator.vault.privacy.domain.interfaces.AnalyticsRepository;
import com.calculator.vault.privacy.domain.interfaces.AppLaunchGateway;
import com.calculator.vault.privacy.domain.interfaces.AppRepository;
import com.calculator.vault.privacy.domain.interfaces.DashboardRepository;
import com.calculator.vault.privacy.domain.interfaces.FileRepository;
import com.calculator.vault.privacy.domain.interfaces.InstalledAppsRepository;
import com.calculator.vault.privacy.domain.interfaces.IntruderRepository;
import com.calculator.vault.privacy.domain.interfaces.NoteRepository;
import com.calculator.vault.privacy.domain.interfaces.NotificationRepository;
import com.calculator.vault.privacy.domain.interfaces.PremiumRepository;
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository;

import com.calculator.vault.privacy.BuildConfig;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class DataModule {
    @Provides
    @Singleton
    static PrivacySpaceDatabase provideDatabase(
            @ApplicationContext Context context,
            DatabaseKeyManager databaseKeyManager
    ) {
        SQLiteDatabase.loadLibs(context);
        SupportFactory factory = new SupportFactory(databaseKeyManager.getOrCreatePassphrase());
        androidx.room.RoomDatabase.Builder<PrivacySpaceDatabase> builder = Room.databaseBuilder(
                context,
                PrivacySpaceDatabase.class,
                "privacy_space.db"
        )
                .openHelperFactory(factory)
                .addMigrations(
                        DatabaseMigrations.MIGRATION_1_2,
                        DatabaseMigrations.MIGRATION_2_3,
                        DatabaseMigrations.MIGRATION_3_4,
                        DatabaseMigrations.MIGRATION_4_5
                );
        if (BuildConfig.DEBUG) {
            builder.allowMainThreadQueries();
        }
        return builder.build();
    }

    @Provides static VaultAppDao provideVaultAppDao(PrivacySpaceDatabase db) { return db.vaultAppDao(); }
    @Provides static SecureNoteDao provideSecureNoteDao(PrivacySpaceDatabase db) { return db.secureNoteDao(); }
    @Provides static VaultNotificationDao provideVaultNotificationDao(PrivacySpaceDatabase db) { return db.vaultNotificationDao(); }
    @Provides static VaultFileDao provideVaultFileDao(PrivacySpaceDatabase db) { return db.vaultFileDao(); }
    @Provides static IntruderLogDao provideIntruderLogDao(PrivacySpaceDatabase db) { return db.intruderLogDao(); }

    @Provides @Singleton static SecurityRepository provideSecurityRepository(SecurityRepositoryImpl impl) { return impl; }
    @Provides @Singleton static NoteRepository provideNoteRepository(NoteRepositoryImpl impl) { return impl; }
    @Provides @Singleton static AppRepository provideAppRepository(AppRepositoryImpl impl) { return impl; }
    @Provides @Singleton static FileRepository provideFileRepository(FileRepositoryImpl impl) { return impl; }
    @Provides @Singleton static NotificationRepository provideNotificationRepository(NotificationRepositoryImpl impl) { return impl; }
    @Provides @Singleton static PremiumRepository providePremiumRepository(PremiumRepositoryImpl impl) { return impl; }
    @Provides @Singleton static DashboardRepository provideDashboardRepository(DashboardRepositoryImpl impl) { return impl; }
    @Provides @Singleton static IntruderRepository provideIntruderRepository(IntruderRepositoryImpl impl) { return impl; }
    @Provides @Singleton static InstalledAppsRepository provideInstalledAppsRepository(InstalledAppsRepositoryImpl impl) { return impl; }
    @Provides @Singleton static AppLaunchGateway provideAppLaunchGateway(AppLaunchGatewayImpl impl) { return impl; }
    @Provides @Singleton static AnalyticsRepository provideAnalyticsRepository(AnalyticsRepositoryImpl impl) { return impl; }
}
