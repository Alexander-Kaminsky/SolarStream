package com.alexander.solarstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexander.solarstream.viewmodel.SolarViewModel
import com.alexander.solarstream.viewmodel.SolarUiState
import com.alexander.solarstream.ui.components.ChargingPulse
import com.alexander.solarstream.ui.components.PowerFlowLine
import com.alexander.solarstream.ui.components.BatteryNest
import com.alexander.solarstream.ui.components.DetailWindow

@Composable
fun SolarDashboard(viewModel: SolarViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDetailWindow by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "SOLAR STREAM",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (state) {
                is SolarUiState.Success -> {
                    val data = state as SolarUiState.Success

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDetailWindow = true },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(
                                        "PANEL OUTPUT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        "${data.voltage}V",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.weight(1f))

                                if (data.isCharging) {
                                    ChargingPulse()
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            PowerFlowLine(isCharging = data.isCharging)

                            Text(
                                text = "Current: ${data.current}A",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("BATTERY NEST PROTOTYPE", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(12.dp))

                    BatteryNest(chargeLevel = (data.voltage / 14.4f).coerceIn(0f, 1f))

                    Spacer(modifier = Modifier.weight(1f))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.fetchFromBackend() },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222))
                        ) {
                            Text("SYNC CLOUD")
                        }

                        Button(
                            onClick = { viewModel.simulateCloudCover() },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                        ) {
                            Text("SIMULATE", color = Color.Black)
                        }
                    }
                }
                is SolarUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.Yellow)
                    }
                }
                is SolarUiState.Error -> {
                    Text("Error: ${(state as SolarUiState.Error).message}", color = Color.Red)
                }
            }
        }

        if (showDetailWindow) {
            DetailWindow(onDismiss = { showDetailWindow = false })
        }
    }
}