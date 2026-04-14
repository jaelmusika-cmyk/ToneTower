package com.tonetower.app

// --- REFRESHED IMPORTS (Fixed Unresolved References) ---
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Icons (Using Extended Icons)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// Standard Preview
import androidx.compose.ui.tooling.preview.Preview

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
    // 1. Form State
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var instrumentModel by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") } // Used below now!

    // 2. Service Selection State
    var needsRestring by remember { mutableStateOf(false) }
    var needsDeepClean by remember { mutableStateOf(false) }
    var needsFretLevel by remember { mutableStateOf(false) }

    // 3. Pricing Logic
    var baseFee by remember { mutableStateOf(1300.0) }

    // Pull the base fee you set in the Admin screen
    LaunchedEffect(Unit) {
        baseFee = ToneRepository.getSetting("setup_base_price", 1300.0)
    }

    // Dynamic Total Calculation
    val totalDisplay = remember(baseFee, needsRestring, needsDeepClean, needsFretLevel) {
        var total = baseFee
        if (needsRestring) total += 200.0
        if (needsDeepClean) total += 300.0
        if (needsFretLevel) total += 1500.0
        total
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("New Setup Intake", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        HorizontalDivider()

        // --- Client & Instrument Info ---
        OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Client Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = clientPhone,
            onValueChange = { clientPhone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = instrumentModel,
            onValueChange = { instrumentModel = it },
            label = { Text("Instrument Model") },
            modifier = Modifier.fillMaxWidth()
        )
        // Resolves the "Unused" warning:
        OutlinedTextField(
            value = serialNumber,
            onValueChange = { serialNumber = it },
            label = { Text("Serial Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Additional Services", style = MaterialTheme.typography.titleMedium)

        // --- Service Checkboxes ---
        ServiceRow("String Change (+₱200)", needsRestring) { needsRestring = it }
        ServiceRow("Deep Cleaning (+₱300)", needsDeepClean) { needsDeepClean = it }
        ServiceRow("Fret Leveling (+₱1500)", needsFretLevel) { needsFretLevel = it }

        Spacer(Modifier.height(8.dp))

        // --- Total Price Card ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Estimated Fee:", style = MaterialTheme.typography.titleLarge)
                Text("₱$totalDisplay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
        }

        // --- Save Button ---
        Button(
            onClick = {
                // We'll call the repository here to save the job
                println("Saving setup for $clientName - Total: ₱$totalDisplay")
                // TODO: ToneRepository.saveSetupJob(...)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Setup Record")
        }
    }
}

@Composable
fun ServiceRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun StudioContent() {
    Text("Studio Booking Management. Track scheduled sessions.")
}