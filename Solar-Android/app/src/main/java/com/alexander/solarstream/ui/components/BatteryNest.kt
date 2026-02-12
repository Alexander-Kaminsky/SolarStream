package com.alexander.solarstream.ui.components
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.ui.unit.dp
@Composable
fun BatteryNest(chargeLevel: Float) { // 0.0 to 1.0
    val animatedProgress by animateFloatAsState(
        targetValue = chargeLevel,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 180.dp)
            .border(4.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animatedProgress)
                .align(Alignment.BottomCenter)
                .background(if (chargeLevel > 0.2f) Color.Green else Color.Red)
        )
    }
}