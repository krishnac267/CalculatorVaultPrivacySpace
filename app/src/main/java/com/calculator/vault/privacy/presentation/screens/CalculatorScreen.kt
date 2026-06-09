package com.calculator.vault.privacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.calculator.vault.privacy.presentation.testing.TestTags
import androidx.compose.ui.unit.dp
import com.calculator.vault.privacy.presentation.viewmodels.CalculatorUiState

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (String) -> Unit,
    onPercent: () -> Unit,
    onSquareRoot: () -> Unit,
    onPower: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    onToggleScientific: () -> Unit,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Calculator", style = MaterialTheme.typography.headlineLarge)
            if (uiState.showBiometricButton) {
                IconButton(onClick = onBiometricClick) {
                    Icon(Icons.Outlined.Fingerprint, contentDescription = "Biometric unlock")
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                text = uiState.display,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .testTag(TestTags.CALC_DISPLAY),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.End,
            )
        }
        OutlinedButton(onClick = onToggleScientific) {
            Text(if (uiState.scientificMode) "Basic Mode" else "Scientific Mode")
        }
        if (uiState.scientificMode) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton("√", onSquareRoot, Modifier.weight(1f))
                CalcButton("^", onPower, Modifier.weight(1f))
            }
        }
        CalcRow(listOf("C" to onClear, "⌫" to onBackspace, "%" to onPercent, "÷" to { onOperator("÷") }))
        CalcRow(listOf("7" to { onDigit("7") }, "8" to { onDigit("8") }, "9" to { onDigit("9") }, "×" to { onOperator("×") }))
        CalcRow(listOf("4" to { onDigit("4") }, "5" to { onDigit("5") }, "6" to { onDigit("6") }, "-" to { onOperator("-") }))
        CalcRow(listOf("1" to { onDigit("1") }, "2" to { onDigit("2") }, "3" to { onDigit("3") }, "+" to { onOperator("+") }))
        CalcRow(listOf("0" to { onDigit("0") }, "." to onDecimal, "=" to onEquals))
    }
}

@Composable
private fun CalcRow(buttons: List<Pair<String, () -> Unit>>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        buttons.forEach { (label, action) ->
            CalcButton(label, action, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CalcButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 4.dp)
            .semantics { contentDescription = "Calculator $label" }
            .testTag(TestTags.calcKey(label)),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (label == "=") MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}
