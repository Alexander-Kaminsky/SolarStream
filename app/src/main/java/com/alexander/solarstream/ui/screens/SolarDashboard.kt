package com.alexander.solarstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexander.solarstream.R
import com.alexander.solarstream.model.Telemetry
import com.alexander.solarstream.ui.components.HardwareNodeCard
import com.alexander.solarstream.ui.components.FlowIndicator
import com.alexander.solarstream.viewmodel.SolarUiState
import com.alexander.solarstream.viewmodel.SolarViewModel
import com.alexander.solarstream.viewmodel.SolarViewModelFactory

enum class BatteryFlowState {
    CHARGING, DISCHARGING, IDLE
}

fun getBatteryState(chargeAmps: Double, loadAmps: Double): BatteryFlowState {
    val netFlow = chargeAmps - loadAmps
    return when {
        netFlow > 0.1 -> BatteryFlowState.CHARGING
        netFlow < -0.1 -> BatteryFlowState.DISCHARGING
        else -> BatteryFlowState.IDLE
    }
}

// 1. THE STATEFUL WRAPPER (Talks to the ViewModel)
@Composable
fun SolarDashboard(
    viewModel: SolarViewModel = viewModel(factory = SolarViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    // Passes the raw state down to the "dumb" UI
    SolarDashboardContent(
        uiState = uiState,
        onSimulateClick = { viewModel.simulateCloudCover() }
    )
}

// 2. THE STATELESS UI (Only knows how to draw data, doesn't know about Firebase)
@Composable
fun SolarDashboardContent(
    uiState: SolarUiState,
    onSimulateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Text(
            text = "LIVE TELEMETRY",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (uiState) {
            is SolarUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            }
            is SolarUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("System Error: ${uiState.message}", color = Color.Red)
                }
            }
            is SolarUiState.Success -> {
                val data = uiState.telemetry
                val batteryState = getBatteryState(data.chargeAmps, data.loadAmps)

                // NODE 1: Solar Input
                HardwareNodeCard(
                    title = "SOLAR PANEL",
                    mainValue = "${data.solarAmps}A",
                    subValue = if (data.solarAmps > 0.1) "Generating" else "Offline / Night",
                    lottieRes = R.raw.weather_sunny,
                    isActive = data.solarAmps > 0.1
                )

                FlowIndicator(isActive = data.solarAmps > 0.1)

                // NODE 2: Battery Core
                val batteryLottie = when (batteryState) {
                    BatteryFlowState.CHARGING -> R.raw.battery_charger
                    BatteryFlowState.DISCHARGING -> R.raw.battery_low
                    BatteryFlowState.IDLE -> R.raw.battery_full
                }

                HardwareNodeCard(
                    title = "BATTERY CORE",
                    mainValue = "${data.batteryPercent}%",
                    subValue = "Bus: ${data.busVoltage}V | Net: ${"%.2f".format(data.chargeAmps - data.loadAmps)}A",
                    lottieRes = batteryLottie,
                    isActive = true
                )

                FlowIndicator(isActive = data.loadAmps > 0.1, isDischarge = true)

                // NODE 3: System Load
                HardwareNodeCard(
                    title = "SYSTEM LOAD",
                    mainValue = "${data.loadAmps}A",
                    subValue = if (data.loadAmps > 0.1) "Discharging" else "Idle",
                    lottieRes = R.raw.mechanics_gears,
                    isActive = data.loadAmps > 0.1
                )

                Spacer(modifier = Modifier.weight(1f))

                // Demo Trigger Button
                Button(
                    onClick = onSimulateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (uiState.isDemo) "DEMO ACTIVE (RESTORE TO CLEAR)" else "SIMULATE CLOUD COVER",
                        color = if (uiState.isDemo) Color(0xFFFFC107) else Color.White
                    )
                }
            }
        }
    }
}

// 3. THE PREVIEW (Injects fake data into the Stateless UI)
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun SolarDashboardPreview() {
    // Create some fake telemetry data that looks good
    val fakeData = Telemetry(
        solarAmps = 1.2,
        chargeAmps = 1.1,
        loadAmps = 0.5,
        batteryPercent = 85,
        busVoltage = 4.02
    )

    // Pass it directly to the stateless content
    SolarDashboardContent(
        uiState = SolarUiState.Success(telemetry = fakeData, isDemo = false),
        onSimulateClick = {} // Do nothing in preview
    )
}