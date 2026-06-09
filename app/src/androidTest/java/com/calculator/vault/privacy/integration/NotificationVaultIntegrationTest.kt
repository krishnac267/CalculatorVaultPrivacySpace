package com.calculator.vault.privacy.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.calculator.vault.privacy.core.session.SessionManager
import com.calculator.vault.privacy.domain.interfaces.NotificationRepository
import com.calculator.vault.privacy.domain.model.SessionState
import com.calculator.vault.privacy.domain.model.VaultScope
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotificationVaultIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var notificationRepository: NotificationRepository
    @Inject lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun storeNotification_respectsVaultScope() {
        sessionManager.unlock(SessionState.REAL_VAULT)
        notificationRepository.storeNotification(
            "com.test.app",
            "Test",
            "Real alert",
            "Body",
            System.currentTimeMillis(),
        )
        sessionManager.unlock(SessionState.FAKE_VAULT)
        val fakeScoped = notificationRepository.getAllNotifications()
        assertThat(fakeScoped).isEmpty()
    }

    @Test
    fun trimHistory_enforcesLimit() {
        sessionManager.unlock(SessionState.REAL_VAULT)
        repeat(5) { index ->
            notificationRepository.storeNotification(
                "com.test",
                "App",
                "Title $index",
                "Body",
                System.currentTimeMillis(),
            )
        }
        notificationRepository.trimHistory(3)
        assertThat(notificationRepository.getNotificationCount()).isAtMost(3)
    }
}
