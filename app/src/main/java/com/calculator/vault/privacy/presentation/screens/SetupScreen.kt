package com.calculator.vault.privacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import com.calculator.vault.privacy.presentation.testing.TestTags
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.presentation.components.OneUiScaffold
import com.calculator.vault.privacy.presentation.viewmodels.SetupUiState

@Composable
fun SetupScreen(
    uiState: SetupUiState,
    onPinChange: (String) -> Unit,
    onConfirmPinChange: (String) -> Unit,
    onFakePinChange: (String) -> Unit,
    onBiometricChange: (Boolean) -> Unit,
    onNext: () -> Unit,
) {
    OneUiScaffold(
        title = "Secure Setup",
        modifier = Modifier.testTag(TestTags.SETUP_TITLE),
        subtitle = when (uiState.step) {
            0 -> "Create your secret PIN"
            1 -> "Confirm your PIN"
            else -> "Optional fake PIN and biometric login"
        },
    ) {
        when (uiState.step) {
            0 -> PinField("Secret PIN", uiState.pin, onPinChange, TestTags.SETUP_PIN)
            1 -> PinField("Confirm PIN", uiState.confirmPin, onConfirmPinChange, TestTags.SETUP_CONFIRM_PIN)
            else -> {
                PinField("Fake PIN (optional)", uiState.fakePin, onFakePinChange, TestTags.SETUP_FAKE_PIN)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enable biometric unlock")
                    Switch(
                        checked = uiState.biometricEnabled,
                        onCheckedChange = onBiometricChange,
                        modifier = Modifier.testTag(TestTags.SETUP_BIOMETRIC),
                    )
                }
            }
        }
        uiState.error?.let { Text(it) }
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.SETUP_NEXT),
        ) {
            Text(if (uiState.step < 2) "Continue" else "Finish Setup")
        }
    }
}

@Composable
private fun PinField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    testTag: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
    )
}
