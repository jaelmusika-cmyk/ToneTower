package com.tonetower.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange

import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
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

@Composable
fun AdminScreen() {
    // State for TextFields
    var setupBasePrice by remember { mutableStateOf("") }
    var studioHourlyRate by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Load data from Database when screen opens
    LaunchedEffect(Unit) {
        setupBasePrice = ToneRepository.getSetting("setup_base_price", 1500.0).toString()
        studioHourlyRate = ToneRepository.getSetting("studio_hourly_rate", 500.0).toString()
    }

    Column(modifier = Modifier.widthIn(max = 400.dp)) {
        Text("Service Rates", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = setupBasePrice,
            onValueChange = { setupBasePrice = it },
            label = { Text("Base Setup Fee (₱)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = studioHourlyRate,
            onValueChange = { studioHourlyRate = it },
            label = { Text("Studio Hourly Rate (₱)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                isSaving = true
                ToneRepository.saveSetting("setup_base_price", setupBasePrice.toDoubleOrNull() ?: 1500.0)
                ToneRepository.saveSetting("studio_hourly_rate", studioHourlyRate.toDoubleOrNull() ?: 500.0)
                isSaving = false
            },
            enabled = !isSaving,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (isSaving) "Saving..." else "Update Rates")
        }
    }
}

@Composable
fun DashboardContent() {
    Text("Welcome to ToneTower. Overview and recent activity will appear here.")
}

@Composable
fun SetupsContent() {
    Text("Guitar Setup Tracker. Log instrument maintenance here.")
}

@Composable
fun StudioContent() {
    Text("Studio Booking Management. Track scheduled sessions.")
}