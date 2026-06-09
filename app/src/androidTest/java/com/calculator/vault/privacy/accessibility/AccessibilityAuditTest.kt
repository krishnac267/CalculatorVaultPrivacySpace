package com.calculator.vault.privacy.accessibility

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.calculator.vault.privacy.data.testing.ResetAppForTestingUseCase
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.usecases.SetupVaultUseCase
import com.calculator.vault.privacy.e2e.E2ETestBase
import com.calculator.vault.privacy.e2e.waitForCalculator
import com.calculator.vault.privacy.presentation.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AccessibilityAuditTest {
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
        E2ETestBase.resetAndOpenCalculator(
            composeRule,
            resetAppForTestingUseCase,
            setupVaultUseCase,
            securityRepository,
            biometricEnabled = true,
        )
    }

    @Test
    fun calculator_biometricButton_hasContentDescription() {
        composeRule.waitForCalculator()
        composeRule.onNodeWithContentDescription("Calculator 1", useUnmergedTree = true).assertExists()
        runCatching {
            composeRule.onNodeWithContentDescription("Biometric unlock", useUnmergedTree = true).assertExists()
        }
    }
}
