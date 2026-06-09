package com.calculator.vault.privacy.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressBar(
    progress: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(800),
        label = "progress",
    )
    Text(text = label, style = MaterialTheme.typography.titleLarge)
    LinearProgressIndicator(
        progress = { animated },
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(8.dp)),
    )
}
