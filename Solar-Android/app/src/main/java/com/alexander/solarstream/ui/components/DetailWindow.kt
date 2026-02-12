package com.alexander.solarstream.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailWindow(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("System Specifications", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("• Hardware: ESP32 + CN3791 MPPT", style = MaterialTheme.typography.bodyMedium)
                Text("• Battery: 4-Cell Parallel (18650 Li-ion)", style = MaterialTheme.typography.bodyMedium)
                Text("• Enclosure: Custom 3D Printed Prototype", style = MaterialTheme.typography.bodyMedium)
                Text("• Backend: Node.js / Firebase Realtime SDK", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Next Phase: Custom PCB for integrated battery nest.", color = Color.Gray)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("CLOSE", color = Color.Yellow) }
        },
        containerColor = Color(0xFF222222),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray
    )
}