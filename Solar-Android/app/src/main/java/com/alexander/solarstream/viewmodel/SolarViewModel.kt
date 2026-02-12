package com.alexander.solarstream.viewmodel
import com.google.gson.Gson
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.alexander.solarstream.model.Telemetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
class SolarViewModel : ViewModel() {
    // Start with the Success state for your "Fool-Proof" Demo
    private val _uiState = MutableStateFlow<SolarUiState>(
        SolarUiState.Success(
            voltage = 13.8f,
            current = 2.4f,
            isCharging = true,
            isDemo = true
        )
    )
    val uiState: StateFlow<SolarUiState> = _uiState.asStateFlow()

    fun simulateCloudCover() {
        _uiState.update { currentState ->
            // Use 'when' or 'if is' to safely access the Success data class
            if (currentState is SolarUiState.Success) {
                currentState.copy(
                    voltage = 12.1f,
                    current = 0.1f,
                    isCharging = false
                )
            } else {
                currentState
            }
        }
    }
    fun fetchFromBackend() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                /*192.168.1.198*/
                val url = URL("http://10.0.2.2:3000/api/v1/telemetry")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseText = connection.inputStream.bufferedReader().use { it.readText() }

                val telemetry = Gson().fromJson(responseText, Telemetry::class.java)

                _uiState.update {
                    SolarUiState.Success(
                        voltage = telemetry.voltage,
                        current = telemetry.current,
                        isCharging = telemetry.current > 0.1f,
                        isDemo = false
                    )
                }
            } catch (e: Exception) {
                Log.e("SOLAR_SYNC", "Backend connection failed: ${e.message}")
            }
        }
    }
}