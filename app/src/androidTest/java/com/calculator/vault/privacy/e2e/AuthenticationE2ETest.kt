package com.calculator.vault.privacy.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.calculator.vault.privacy.data.testing.ResetAppForTestingUseCase
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.usecases.SetupVaultUseCase
import com.calculator.vault.privacy.presentation.MainActivity
import com.calculator.vault.privacy.presentation.testing.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthenticationE2ETest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var resetAppForTestingUseCase: ResetAppForTestingUseCase
    @Inject lateinit var setupVaultUseCase: SetupVaultUseCase
    @Inject lateinit var securityRepository: SecurityRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun firstLaunch_showsSetup_thenCalculatorAfterFinish() {
        E2ETestBase.resetApp(composeRule, resetAppForTestingUseCase)
        composeRule.completeSetup(pin = "5678")
        composeRule.assertOnCalculator()
    }

    @Test
    fun realPinUnlock_opensVaultHome() {
        E2ETestBase.resetAndOpenCalculator(
            composeRule,
            resetAppForTestingUseCase,
            setupVaultUseCase,
            securityRepository,
            pin = "1234",
        )
        E2ETestBase.unlockRealVault(composeRule, "1234")
        composeRule.onNodeWithTag(TestTags.VAULT_TAB_HOME, useUnmergedTree = true).assertExists()
    }

    @Test
    fun fakePinUnlock_opensFakeVault() {
        E2ETestBase.resetAndOpenCalculator(
            composeRule,
            resetAppForTestingUseCase,
            setupVaultUseCase,
            securityRepository,
            pin = "1234",
            fakePin = "4321",
        )
        composeRule.enterPin("4321")
        composeRule.waitForVaultHome()
    }

    @Test
    fun backgroundLock_returnsToCalculatorAndHidesVault() {
        E2ETestBase.resetAndOpenCalculator(
            composeRule,
            resetAppForTestingUseCase,
            setupVaultUseCase,
            securityRepository,
        )
        E2ETestBase.unlockRealVault(composeRule)
        composeRule.onNodeWithTag(TestTags.VAULT_TAB_NOTES, useUnmergedTree = true).performClick()
        E2ETestBase.lockViaBackground(composeRule, securityRepository)
        composeRule.assertOnCalculator()
    }
}
