package com.alexander.solarstream.viewmodel

sealed class SolarUiState {
    object Loading : SolarUiState()

    data class Success(
        val voltage: Float,
        val current: Float,
        val isCharging: Boolean,
        val isDemo: Boolean = false
    ) : SolarUiState()

    data class Error(val message: String) : SolarUiState()
}