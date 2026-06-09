package com.calculator.vault.privacy.e2e

import android.content.Intent
import android.view.KeyEvent
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.calculator.vault.privacy.data.testing.ResetAppForTestingUseCase
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.usecases.SetupVaultUseCase
import com.calculator.vault.privacy.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object E2ETestBase {
    /**
     * Restarts MainActivity without restoring navigation state. `recreate()` keeps the
     * back stack on the setup screen even after vault data is seeded programmatically.
     */
    private fun relaunchMainActivity(
        composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
    ) {
        composeRule.activityRule.scenario.onActivity { activity ->
            val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)!!
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
            activity.finish()
        }
        composeRule.waitForIdle()
    }

    fun resetApp(
        composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
        resetAppForTestingUseCase: ResetAppForTestingUseCase,
    ) {
        runBlocking(Dispatchers.Default) {
            resetAppForTestingUseCase.execute()
        }
        relaunchMainActivity(composeRule)
        composeRule.waitForSetup()
    }

    fun seedVault(
        setupVaultUseCase: SetupVaultUseCase,
        securityRepository: SecurityRepository,
        pin: String = "1234",
        fakePin: String? = null,
        biometricEnabled: Boolean = false,
    ) {
        setupVaultUseCase.execute(pin, fakePin, biometricEnabled)
        securityRepository.setIntruderCaptureEnabled(false)
    }

    fun resetAndOpenCalculator(
        composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
        resetAppForTestingUseCase: ResetAppForTestingUseCase,
        setupVaultUseCase: SetupVaultUseCase,
        securityRepository: SecurityRepository,
        pin: String = "1234",
        fakePin: String? = null,
        biometricEnabled: Boolean = false,
    ) {
        runBlocking(Dispatchers.Default) {
            resetAppForTestingUseCase.execute()
            seedVault(setupVaultUseCase, securityRepository, pin, fakePin, biometricEnabled)
        }
        relaunchMainActivity(composeRule)
        composeRule.waitForCalculator()
    }

    fun unlockRealVault(
        composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
        pin: String = "1234",
    ) {
        composeRule.enterPin(pin)
        composeRule.waitForVaultHome()
    }

    fun lockViaBackground(
        composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
        securityRepository: SecurityRepository,
    ) {
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_HOME)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            securityRepository.getSessionState().name == "LOCKED"
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        composeRule.waitForIdle()
        composeRule.waitForCalculator()
    }
}
