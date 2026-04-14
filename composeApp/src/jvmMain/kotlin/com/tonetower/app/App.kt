package com.tonetower.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Specific imports for Material Icons to resolve the "Unresolved reference"
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange

// Correct Preview import for modern Compose Multiplatform
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // AppScreen is pulled from your Models.kt file
        var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // --- SIDEBAR NAVIGATION ---
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    header = {
                        Text(
                            "TT",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                ) {
                    NavigationRailItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { currentScreen = AppScreen.DASHBOARD },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == AppScreen.SETUPS,
                        onClick = { currentScreen = AppScreen.SETUPS },
                        icon = { Icon(imageVector = Icons.Default.Build, contentDescription = "Setups") },
                        label = { Text("Setups") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == AppScreen.STUDIO,
                        onClick = { currentScreen = AppScreen.STUDIO },
                        icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Studio") },
                        label = { Text("Studio") }
                    )

                    Spacer(Modifier.weight(1f))

                    NavigationRailItem(
                        selected = currentScreen == AppScreen.ADMIN,
                        onClick = { currentScreen = AppScreen.ADMIN },
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Admin") },
                        label = { Text("Admin") }
                    )
                }

                // --- MAIN CONTENT AREA ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = currentScreen.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardContent()
                        AppScreen.SETUPS -> SetupsContent()
                        AppScreen.STUDIO -> StudioContent()
                        AppScreen.ADMIN -> AdminScreen()
                    }
                }
            }
        }
    }
}

// --- SCREEN PLACEHOLDERS ---

@Composable
fun DashboardContent() {
    Text("Overview and recent activity will appear here.")
}

@Composable
fun SetupsContent() {
    Text("Guitar Setup Tracker.")
}

@Composable
fun StudioContent() {
    Text("Studio Booking Management.")
}

@Composable
fun AdminScreen() {
    Text("Admin Control Panel.")
}