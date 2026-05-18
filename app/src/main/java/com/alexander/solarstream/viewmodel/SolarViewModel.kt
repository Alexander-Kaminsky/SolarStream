package com.alexander.solarstream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexander.solarstream.data.SolarRepository
import com.alexander.solarstream.model.Telemetry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SolarViewModel : ViewModel() {

    private val repository = SolarRepository.getInstance()

    // Start with Loading state until Firebase connects
    private val _uiState = MutableStateFlow<SolarUiState>(SolarUiState.Loading)
    val uiState: StateFlow<SolarUiState> = _uiState.asStateFlow()

    init {
        // Automatically connect to the backend simulator on boot
        startFirebaseStream()
    }

    private fun startFirebaseStream() {
        viewModelScope.launch {
            repository.getSolarData()
                .catch { e ->
                    _uiState.value = SolarUiState.Error("Firebase Sync Failed: ${e.message}")
                }
                .collect { incomingData ->
                    if (incomingData != null) {
                        _uiState.value = SolarUiState.Success(
                            telemetry = incomingData,
                            isDemo = false
                        )
                    } else {
                        _uiState.value = SolarUiState.Error("System Offline")
                    }
                }
        }
    }

    // Updated your demo function to use the new Telemetry model
    fun simulateCloudCover() {
        _uiState.update { currentState ->
            if (currentState is SolarUiState.Success) {
                // Drop solar input to near zero, simulating a cloud
                val demoTelemetry = currentState.telemetry.copy(
                    solarAmps = 0.1,
                    chargeAmps = 0.0, // No charging happening
                    busVoltage = 3.6 // Voltage drops a bit without solar support
                )

                SolarUiState.Success(
                    telemetry = demoTelemetry,
                    isDemo = true
                )
            } else {
                currentState
            }
        }
    }
}