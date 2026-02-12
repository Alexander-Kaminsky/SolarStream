package com.alexander.solarstream.model

data class Telemetry(
    val voltage: Float = 0f,
    val current: Float = 0f,
    val watts: Float = 0f
)