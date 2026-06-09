package com.calculator.vault.privacy.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.calculator.vault.privacy.data.database.dao.SecureNoteDao
import com.calculator.vault.privacy.data.testing.ResetAppForTestingUseCase
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.model.VaultScope
import com.calculator.vault.privacy.domain.usecases.SetupVaultUseCase
import com.calculator.vault.privacy.presentation.MainActivity
import com.calculator.vault.privacy.presentation.testing.TestTags
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
class NotesE2ETest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var resetAppForTestingUseCase: ResetAppForTestingUseCase
    @Inject lateinit var setupVaultUseCase: SetupVaultUseCase
    @Inject lateinit var securityRepository: SecurityRepository
    @Inject lateinit var secureNoteDao: SecureNoteDao

    @Before
    fun setUp() {
        hiltRule.inject()
        E2ETestBase.resetAndOpenCalculator(composeRule, resetAppForTestingUseCase, setupVaultUseCase, securityRepository)
        E2ETestBase.unlockRealVault(composeRule)
        composeRule.openVaultTab(TestTags.VAULT_TAB_NOTES)
    }

    @Test
    fun createNote_persistsEncryptedContentInDatabase() {
        composeRule.onNodeWithTag(TestTags.NOTES_CREATE, useUnmergedTree = true).performClick()
        composeRule.fillNoteEditor(title = "QA Note", content = "Sensitive payload")
        composeRule.saveNoteEditor()

        composeRule.waitUntil(timeoutMillis = 15_000) {
            secureNoteDao.getAll(VaultScope.REAL).isNotEmpty()
        }
        val entity = secureNoteDao.getAll(VaultScope.REAL).first()
        assertThat(entity.encryptedContent).isNotNull()
        assertThat(entity.encryptedContent).doesNotContain("Sensitive payload")
        assertThat(entity.content).isEmpty()
    }

    @Test
    fun lockedNote_requiresPinDialog() {
        composeRule.onNodeWithTag(TestTags.NOTES_CREATE, useUnmergedTree = true).performClick()
        composeRule.fillNoteEditor(title = "Locked", content = "Hidden", lock = true)
        composeRule.saveNoteEditor()

        composeRule.waitUntil(timeoutMillis = 15_000) {
            runCatching {
                composeRule.onNodeWithText("Locked", useUnmergedTree = true).assertExists()
                true
            }.getOrDefault(false)
        }
        composeRule.onNodeWithText("Locked", useUnmergedTree = true).performClick()
        composeRule.waitUntil(timeoutMillis = 15_000) {
            runCatching {
                composeRule.onNodeWithTag(TestTags.PIN_UNLOCK_DIALOG, useUnmergedTree = true).assertExists()
                true
            }.getOrDefault(false)
        }
    }
}
