package com.alexander.solarstream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Used to instantiate the SolarViewModel and inject dependencies cleanly.
 */
class SolarViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolarViewModel::class.java)) {
            return SolarViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}