package com.alexander.solarstream.model

data class SolarState(
    val voltage: Float = 0f,
    val current: Float = 0f,
    val isConnected: Boolean = false,
    val isDemoMode: Boolean = false
)