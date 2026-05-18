package com.alexander.solarstream.viewmodel

import com.alexander.solarstream.model.Telemetry

sealed class SolarUiState {
    object Loading : SolarUiState()

    // Holds the full 5-channel payload and your demo flag
    data class Success(
        val telemetry: Telemetry,
        val isDemo: Boolean = false,
    ) : SolarUiState()

    data class Error(val message: String) : SolarUiState()
}