package com.calculator.vault.privacy.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.calculator.vault.privacy.core.security.ContentEncryptionService
import com.calculator.vault.privacy.core.security.PinManager
import com.calculator.vault.privacy.data.database.dao.SecureNoteDao
import com.calculator.vault.privacy.data.database.entity.SecureNoteEntity
import com.calculator.vault.privacy.data.migration.NoteEncryptionMigrator
import com.calculator.vault.privacy.data.testing.ResetAppForTestingUseCase
import com.calculator.vault.privacy.domain.model.VaultScope
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.usecases.SetupVaultUseCase
import com.calculator.vault.privacy.e2e.assertVaultContentHidden
import com.calculator.vault.privacy.e2e.enterPin
import com.calculator.vault.privacy.e2e.waitForVaultHome
import com.calculator.vault.privacy.presentation.MainActivity
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
class FakeVaultIsolationE2ETest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var resetAppForTestingUseCase: ResetAppForTestingUseCase
    @Inject lateinit var setupVaultUseCase: SetupVaultUseCase
    @Inject lateinit var securityRepository: SecurityRepository
    @Inject lateinit var secureNoteDao: SecureNoteDao
    @Inject lateinit var encryptionService: ContentEncryptionService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun fakeVault_doesNotSeeRealNotes() {
        E2ETestBase.resetAndOpenCalculator(
            composeRule,
            resetAppForTestingUseCase,
            setupVaultUseCase,
            securityRepository,
            pin = "1234",
            fakePin = "9999",
        )
        seedRealNote("Real secret", "Real content")
        composeRule.enterPin("9999")
        composeRule.waitForVaultHome()

        assertThat(secureNoteDao.getAll(VaultScope.FAKE).map { it.title })
            .doesNotContain("Real secret")
        assertThat(secureNoteDao.getAll(VaultScope.REAL).map { it.title })
            .contains("Real secret")
        composeRule.assertVaultContentHidden("Real secret")
    }

    private fun seedRealNote(title: String, content: String) {
        val entity = SecureNoteEntity()
        entity.title = title
        entity.content = ""
        entity.encryptedContent = encryptionService.encryptText(content)
        entity.searchText = NoteEncryptionMigrator.buildSearchText(title, content)
        entity.favorite = false
        entity.locked = false
        entity.vaultScope = VaultScope.REAL
        entity.createdAt = System.currentTimeMillis()
        entity.updatedAt = entity.createdAt
        secureNoteDao.insert(entity)
    }
}
