package com.tonetower.app

// --- REFRESHED IMPORTS ---
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
import androidx.compose.foundation.shape.RoundedCornerShape

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun App() {
    MaterialTheme {
        // AppScreen is pulled from Models.kt
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
                        icon = { Icon(Icons.Default.Home, "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == AppScreen.SETUPS,
                        onClick = { currentScreen = AppScreen.SETUPS },
                        icon = { Icon(Icons.Default.Build, "Setups") },
                        label = { Text("Setups") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == AppScreen.STUDIO,
                        onClick = { currentScreen = AppScreen.STUDIO },
                        icon = { Icon(Icons.Default.DateRange, "Studio") },
                        label = { Text("Studio") }
                    )

                    Spacer(Modifier.weight(1f))

                    NavigationRailItem(
                        selected = currentScreen == AppScreen.ADMIN,
                        onClick = { currentScreen = AppScreen.ADMIN },
                        icon = { Icon(Icons.Default.Settings, "Admin") },
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
fun SetupsContent() {
    // 1. Form State
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var instrumentModel by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }

    // 2. Service Selection State
    var needsRestring by remember { mutableStateOf(false) }
    var needsDeepClean by remember { mutableStateOf(false) }
    var needsFretLevel by remember { mutableStateOf(false) }

    // 3. History State
    var history by remember { mutableStateOf(listOf<SetupJob>()) }

    // 4. Pricing Logic
    var baseFee by remember { mutableStateOf(1300.0) }

    // 5. Phase B Logistics State
    var inboundMethod by remember { mutableStateOf("Walk-in") }
    var logisticsInfo by remember { mutableStateOf("") }
    val logisticsOptions = listOf("Walk-in", "Courier (Grab/Lalamove)", "Shipping (LBC/JRS)")
    var expandedLogistics by remember { mutableStateOf(false) }

    // 6. Phase B Financial State
    var paymentMode by remember { mutableStateOf("Cash") }
    val paymentOptions = listOf("Cash", "GCash", "Bank Transfer")
    var expandedPayment by remember { mutableStateOf(false) }
    var amountTendered by remember { mutableStateOf("") }

    // Helper to refresh data from DB
    fun refreshData() {
        history = ToneRepository.getAllSetupJobs()
    }

    LaunchedEffect(Unit) {
        baseFee = ToneRepository.getSetting("setup_base_price", 1300.0)
        refreshData()
    }

    // Dynamic Calculations
    val totalDisplay = remember(baseFee, needsRestring, needsDeepClean, needsFretLevel) {
        var total = baseFee
        if (needsRestring) total += 200.0
        if (needsDeepClean) total += 300.0
        if (needsFretLevel) total += 1500.0
        total
    }

    val amtDouble = amountTendered.toDoubleOrNull() ?: 0.0
    val changeDue = if (amtDouble > 0) amtDouble - totalDisplay else 0.0

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("New Setup Intake", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        HorizontalDivider()

        // --- Client & Instrument Info ---
        OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = clientPhone, onValueChange = { clientPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        OutlinedTextField(value = instrumentModel, onValueChange = { instrumentModel = it }, label = { Text("Instrument Model") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = serialNumber, onValueChange = { serialNumber = it }, label = { Text("Serial Number") }, modifier = Modifier.fillMaxWidth())

        // --- Logistics Row ---
        Text("Logistics", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { expandedLogistics = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text(inboundMethod)
                }
                DropdownMenu(expanded = expandedLogistics, onDismissRequest = { expandedLogistics = false }) {
                    logisticsOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            inboundMethod = option
                            expandedLogistics = false
                        })
                    }
                }
            }
            OutlinedTextField(
                value = logisticsInfo,
                onValueChange = { logisticsInfo = it },
                label = { Text("Rider / Tracking Details") },
                modifier = Modifier.weight(1.5f),
                singleLine = true
            )
        }

        // --- Service Checkboxes ---
        Text("Additional Services", style = MaterialTheme.typography.titleMedium)
        ServiceRow("String Change (+₱200)", needsRestring) { needsRestring = it }
        ServiceRow("Deep Cleaning (+₱300)", needsDeepClean) { needsDeepClean = it }
        ServiceRow("Fret Leveling (+₱1500)", needsFretLevel) { needsFretLevel = it }

        // --- Payment Section ---
        Text("Payment Details", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { expandedPayment = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text(paymentMode)
                }
                DropdownMenu(expanded = expandedPayment, onDismissRequest = { expandedPayment = false }) {
                    paymentOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            paymentMode = option
                            expandedPayment = false
                        })
                    }
                }
            }
            OutlinedTextField(
                value = amountTendered,
                onValueChange = { amountTendered = it },
                label = { Text("Amount Tendered (₱)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        if (changeDue > 0) {
            Text("Change Due: ₱${String.format("%.2f", changeDue)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        // --- Total Price Card ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Total Fee:", style = MaterialTheme.typography.titleLarge)
                Text("₱$totalDisplay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
        }

        // --- Save Button ---
        Button(
            onClick = {
                val services = listOfNotNull(
                    if (needsRestring) "Restring" else null,
                    if (needsDeepClean) "Deep Clean" else null,
                    if (needsFretLevel) "Fret Level" else null
                ).joinToString(", ")

                val job = SetupJob(
                    referenceId = ToneRepository.generateReferenceId(),
                    clientName = clientName,
                    clientPhone = clientPhone,
                    instrumentModel = instrumentModel,
                    serialNumber = serialNumber,
                    dateAdded = System.currentTimeMillis(),
                    totalFee = totalDisplay,
                    servicesDone = services,
                    status = "Pending",
                    inboundMethod = inboundMethod,
                    logisticsInfo = logisticsInfo,
                    paymentMode = paymentMode,
                    amountTendered = amtDouble,
                    changeDue = changeDue
                )

                ToneRepository.saveSetupJob(job)

                // Reset Form
                clientName = ""; clientPhone = ""; instrumentModel = ""; serialNumber = ""
                logisticsInfo = ""; amountTendered = ""
                needsRestring = false; needsDeepClean = false; needsFretLevel = false
                refreshData()
            },
            enabled = clientName.isNotBlank() && instrumentModel.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Setup Record")
        }

        Spacer(Modifier.height(24.dp))
        Text("Recent History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        HorizontalDivider()

        if (history.isEmpty()) {
            Text("No recent setups found.", color = MaterialTheme.colorScheme.outline)
        } else {
            history.forEach { job ->
                HistoryCard(job, onStatusUpdate = { refreshData() })
            }
        }
    }
}

@Composable
fun HistoryCard(job: SetupJob, onStatusUpdate: () -> Unit) {
    // Helper to generate the text block for sharing/copying
    fun generateProfessionalReceipt(job: SetupJob): String {
        val vatAmount = job.totalFee * 0.1071 // 12% VAT-inclusive calculation
        val netOfVat = job.totalFee - vatAmount

        return """
            TONETOWER GUITAR REPAIR & STUDIO
            Taytay, Rizal | TIN: 000-000-000-000
            ------------------------------------------
            SERVICE SUMMARY: ${job.referenceId}
            
            CUSTOMER: ${job.clientName}
            UNIT: ${job.instrumentModel} (S/N: ${job.serialNumber})
            ------------------------------------------
            SERVICES RENDERED:
            ${job.servicesDone}
            
            Subtotal (Net of VAT): ₱${String.format("%.2f", netOfVat)}
            VAT (12%):             ₱${String.format("%.2f", vatAmount)}
            TOTAL AMOUNT DUE:      ₱${String.format("%.2f", job.totalFee)}
            ------------------------------------------
            PAYMENT: ${job.paymentMode}
            AMOUNT TENDERED: ₱${String.format("%.2f", job.amountTendered)}
            CHANGE:          ₱${String.format("%.2f", job.changeDue)}
            
            LOGISTICS: ${job.inboundMethod}
            ------------------------------------------
            Thank you for trusting ToneTower!
        """.trimIndent()
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(job.referenceId, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (job.status == "Pending") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(job.status.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(job.clientName, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                Text("₱${job.totalFee}", fontWeight = FontWeight.Bold)
            }
            Text("${job.instrumentModel} • ${job.inboundMethod} (${job.paymentMode})", style = MaterialTheme.typography.bodySmall)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // EXPORT BUTTON: Prints receipt to console (you can add Clipboard logic here)
                OutlinedButton(
                    onClick = { println(generateProfessionalReceipt(job)) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Export Receipt")
                }

                if (job.status == "Pending") {
                    Button(
                        onClick = {
                            ToneRepository.updateJobStatus(job.id, "Completed")
                            onStatusUpdate()
                        }
                    ) {
                        Text("Mark as Complete")
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun AdminScreen() {
    var setupBasePrice by remember { mutableStateOf("") }
    var studioHourlyRate by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        setupBasePrice = ToneRepository.getSetting("setup_base_price", 1500.0).toString()
        studioHourlyRate = ToneRepository.getSetting("studio_hourly_rate", 500.0).toString()
    }

    Column(modifier = Modifier.widthIn(max = 400.dp)) {
        Text("Service Rates", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = setupBasePrice, onValueChange = { setupBasePrice = it }, label = { Text("Base Setup Fee (₱)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = studioHourlyRate, onValueChange = { studioHourlyRate = it }, label = { Text("Studio Hourly Rate (₱)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
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
fun DashboardContent() { Text("Welcome to ToneTower. Overview and recent activity will appear here.") }
@Composable
fun StudioContent() { Text("Studio Booking Management. Track scheduled sessions.") }

fun generateProfessionalReceipt(job: SetupJob): String {
    val vatAmount = job.totalFee * 0.1071 // Calculates VAT if you are VAT-inclusive
    val netOfVat = job.totalFee - vatAmount

    return """
        TONETOWER GUITAR REPAIR & STUDIO
        Taytay, Rizal | TIN: [Your-TIN-Here]
        ------------------------------------------
        SERVICE SUMMARY: ${job.referenceId}
        Date: ${java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.format(java.time.LocalDate.now())}
        
        CUSTOMER: ${job.clientName}
        UNIT: ${job.instrumentModel} (S/N: ${job.serialNumber})
        ------------------------------------------
        SERVICES RENDERED:
        ${job.servicesDone}
        
        Subtotal (Net of VAT): ₱${String.format("%.2f", netOfVat)}
        VAT (12%):             ₱${String.format("%.2f", vatAmount)}
        TOTAL AMOUNT DUE:      ₱${String.format("%.2f", job.totalFee)}
        ------------------------------------------
        PAYMENT: ${job.paymentMode}
        AMOUNT TENDERED: ₱${String.format("%.2f", job.amountTendered)}
        CHANGE:          ₱${String.format("%.2f", job.changeDue)}
        
        LOGISTICS: ${job.inboundMethod}
        DETAILS: ${job.logisticsInfo}
        ------------------------------------------
        Thank you for trusting ToneTower!
        This serves as a Service Summary only.
    """.trimIndent()
}