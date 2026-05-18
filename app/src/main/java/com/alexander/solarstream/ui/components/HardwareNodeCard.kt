package com.alexander.solarstream.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun HardwareNodeCard(
    title: String,
    mainValue: String,
    subValue: String,
    lottieRes: Int,
    isActive: Boolean
) {
    // 1. Load the specific Lottie file passed from the Dashboard
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))

    // 2. Control Playback: Pause the animation if the node is inactive (e.g., night time)
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isActive
    )

    // 3. Control Opacity: Smoothly fade the entire card to 40% when inactive
    val nodeAlpha by animateFloatAsState(
        targetValue = if (isActive) 1.0f else 0.4f,
        animationSpec = tween(durationMillis = 800),
        label = "AlphaAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(nodeAlpha),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Text(mainValue, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(subValue, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun FlowIndicator(isActive: Boolean, isDischarge: Boolean = false) {
    val indicatorColor = if (!isActive) Color.DarkGray else if (isDischarge) Color(0xFFE53935) else Color(0xFF4CAF50)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider(
            color = indicatorColor,
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
        )
    }
}