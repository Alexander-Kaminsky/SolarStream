package com.alexander.solarstream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.alexander.solarstream.ui.theme.SolarStreamTheme
import com.alexander.solarstream.ui.screens.MainAppScreen // CRITICAL: Import the Navigation Shell

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarStreamTheme {
                // Surface ensures the default background matches your theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Hand over control to the Navigation Scaffold
                    MainAppScreen()
                }
            }
        }
    }
}