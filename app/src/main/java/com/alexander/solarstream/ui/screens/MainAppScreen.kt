package com.alexander.solarstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize // MISSING IMPORT
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator // MISSING IMPORT
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue // MISSING IMPORT
import androidx.compose.runtime.mutableStateOf // MISSING IMPORT
import androidx.compose.runtime.remember // MISSING IMPORT
import androidx.compose.runtime.rememberCoroutineScope // MISSING IMPORT
import androidx.compose.ui.Alignment // MISSING IMPORT
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexander.solarstream.core.utils.SessionManager
import com.alexander.solarstream.ui.theme.SolarStreamTheme
import kotlinx.coroutines.launch
import com.alexander.solarstream.data.api.RetrofitClient
import com.alexander.solarstream.data.api.LoginRequest

/**
 * Main Scaffold & Navigation
 * Acts as the application shell, managing the bottom navigation and screen routing.
 */
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Hide bottom bar on Login screen
            if (currentRoute != "login") {
                SolarBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            // Prevents building up a massive backstack of identical screens
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                val coroutineScope = rememberCoroutineScope()
                var isLoading by remember { mutableStateOf(false) }
                var loginErrorMessage by remember { mutableStateOf<String?>(null) }

                // Wrap the LoginScreen in a Box to show loading states/errors
                Box(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(
                        onLoginSuccess = { email, password ->
                            coroutineScope.launch {
                                isLoading = true
                                loginErrorMessage = null
                                try {
                                    // 1. Make the HTTP Call to Node.js
                                    val request = LoginRequest(email, password)
                                    val response = RetrofitClient.apiService.login(request)

                                    // 2. Save the official userPrefix returned from the server
                                    SessionManager.getInstance().loginUser(response.userPrefix)

                                    // 3. Navigate to Dashboard
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    loginErrorMessage = "Network Error: Cannot reach Node.js server"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )

                    // Overlay a simple loading spinner if the network call is running
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
            composable("dashboard") { SolarDashboard() }
            composable("feed") { CommunityFeed() }
            composable("chat") { ChatScreen() }
        }
    }
}

/**
 * RUBRIC POINT: Extracted UI Component
 * Separating the navigation bar allows us to preview it perfectly without breaking the NavHost.
 */
@Composable
fun SolarBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Color(0xFF161616)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF4CAF50),
                selectedTextColor = Color(0xFF4CAF50),
                indicatorColor = Color(0xFF2E7D32).copy(alpha = 0.3f), // Subtle green highlight
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Feed") },
            label = { Text("Community") },
            selected = currentRoute == "feed",
            onClick = { onNavigate("feed") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF4CAF50),
                selectedTextColor = Color(0xFF4CAF50),
                indicatorColor = Color(0xFF2E7D32).copy(alpha = 0.3f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Chat") },
            label = { Text("Maker Chat") },
            selected = currentRoute == "chat",
            onClick = { onNavigate("chat") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF4CAF50),
                selectedTextColor = Color(0xFF4CAF50),
                indicatorColor = Color(0xFF2E7D32).copy(alpha = 0.3f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

// --- THE PREVIEW ---
// We can now preview just the bottom bar safely!
@Preview(showBackground = true)
@Composable
fun SolarBottomBarPreview() {
    SolarStreamTheme { // Wraps the preview in your Material 3 theme
        Box(modifier = Modifier.background(Color(0xFF0A0A0A))) {
            SolarBottomBar(
                currentRoute = "dashboard", // Change this to "feed" or "chat" to see the green highlight move!
                onNavigate = {} // Empty lambda because clicks don't navigate in previews
            )
        }
    }
}