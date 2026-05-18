package com.alexander.solarstream.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data Model for incoming Firebase Telemetry.
 * Matches the Node.js backend Simulator payload exactly.
 */
@IgnoreExtraProperties
data class Telemetry(
    val solarAmps: Double = 0.0,
    val chargeAmps: Double = 0.0,
    val loadAmps: Double = 0.0,
    val batteryPercent: Int = 0,
    val busVoltage: Double = 0.0,
    val timestamp: Long = 0L
)