package com.alexander.solarstream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alexander.solarstream.ui.theme.SolarStreamTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexander.solarstream.ui.screens.SolarDashboard
import com.alexander.solarstream.viewmodel.SolarViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarStreamTheme {
                val viewModel: SolarViewModel = viewModel()
                SolarDashboard(viewModel = viewModel)
            }
        }
    }
}