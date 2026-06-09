package com.calculator.vault.privacy.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.calculator.vault.privacy.presentation.MainActivity
import com.calculator.vault.privacy.presentation.testing.TestTags

private const val USE_UNMERGED = true

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.waitForCalculator() {
    waitUntil(timeoutMillis = 30_000) {
        runCatching {
            onNodeWithTag(TestTags.CALC_DISPLAY, USE_UNMERGED).assertExists()
            true
        }.getOrElse {
            runCatching {
                onNodeWithText("0", USE_UNMERGED).assertExists()
                true
            }.getOrDefault(false)
        }
    }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.waitForSetup() {
    waitUntil(timeoutMillis = 30_000) {
        runCatching {
            onNodeWithTag(TestTags.SETUP_TITLE, USE_UNMERGED).assertExists()
            true
        }.getOrElse {
            runCatching {
                onNodeWithText("Secure Setup", USE_UNMERGED).assertExists()
                true
            }.getOrDefault(false)
        }
    }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.completeSetup(
    pin: String = "1234",
    fakePin: String? = null,
) {
    waitForSetup()
    onNodeWithTag(TestTags.SETUP_PIN, USE_UNMERGED).performClick()
    onNodeWithTag(TestTags.SETUP_PIN, USE_UNMERGED).performTextInput(pin)
    onNodeWithTag(TestTags.SETUP_NEXT, USE_UNMERGED).performClick()

    onNodeWithTag(TestTags.SETUP_CONFIRM_PIN, USE_UNMERGED).performClick()
    onNodeWithTag(TestTags.SETUP_CONFIRM_PIN, USE_UNMERGED).performTextInput(pin)
    onNodeWithTag(TestTags.SETUP_NEXT, USE_UNMERGED).performClick()

    if (fakePin != null) {
        onNodeWithTag(TestTags.SETUP_FAKE_PIN, USE_UNMERGED).performClick()
        onNodeWithTag(TestTags.SETUP_FAKE_PIN, USE_UNMERGED).performTextInput(fakePin)
    }
    onNodeWithTag(TestTags.SETUP_NEXT, USE_UNMERGED).performClick()
    waitForCalculator()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.tapCalc(label: String) {
    onNodeWithTag(TestTags.calcKey(label), USE_UNMERGED).performClick()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.enterPin(pin: String) {
    pin.forEach { digit -> tapCalc(digit.toString()) }
    tapCalc("=")
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.waitForVaultHome() {
    waitUntil(timeoutMillis = 15_000) {
        runCatching {
            onNodeWithText("Quick Actions", USE_UNMERGED).assertIsDisplayed()
            true
        }.getOrDefault(false)
    }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.openVaultTab(tag: String) {
    onNodeWithTag(tag, USE_UNMERGED).performClick()
    waitForIdle()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertOnCalculator() {
    onNodeWithTag(TestTags.CALC_DISPLAY, USE_UNMERGED).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertVaultContentHidden(
    secret: String,
) {
    waitForIdle()
    runCatching { onNodeWithText(secret, USE_UNMERGED).assertIsDisplayed() }
        .onFailure { return }
    throw AssertionError("Vault content '$secret' visible while locked")
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertDisplay(expected: String) {
    onNodeWithTag(TestTags.CALC_DISPLAY, USE_UNMERGED).assertIsDisplayed()
    waitUntil(timeoutMillis = 5_000) {
        runCatching {
            onNodeWithTag(TestTags.CALC_DISPLAY, USE_UNMERGED).assertTextEquals(expected)
            true
        }.getOrDefault(false)
    }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.fillNoteEditor(
    title: String,
    content: String,
    lock: Boolean = false,
) {
    onNodeWithTag(TestTags.NOTE_TITLE, USE_UNMERGED).performScrollTo()
    onNodeWithTag(TestTags.NOTE_TITLE, USE_UNMERGED).performClick()
    onNodeWithTag(TestTags.NOTE_TITLE, USE_UNMERGED).performTextReplacement(title)
    onNodeWithTag(TestTags.NOTE_CONTENT, USE_UNMERGED).performScrollTo()
    onNodeWithTag(TestTags.NOTE_CONTENT, USE_UNMERGED).performClick()
    onNodeWithTag(TestTags.NOTE_CONTENT, USE_UNMERGED).performTextReplacement(content)
    if (lock) {
        onNodeWithTag(TestTags.NOTE_LOCK, USE_UNMERGED).performScrollTo()
        onNodeWithTag(TestTags.NOTE_LOCK, USE_UNMERGED).performClick()
    }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.saveNoteEditor() {
    onNodeWithTag(TestTags.NOTE_SAVE, USE_UNMERGED).performScrollTo()
    onNodeWithTag(TestTags.NOTE_SAVE, USE_UNMERGED).performClick()
    waitUntil(timeoutMillis = 15_000) {
        runCatching {
            onNodeWithTag(TestTags.NOTES_CREATE, USE_UNMERGED).assertExists()
            true
        }.getOrDefault(false)
    }
}
