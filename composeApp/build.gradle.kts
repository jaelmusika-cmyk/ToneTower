plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.material3)

            // ADD THIS LINE TO FIX THE "UNRESOLVED REFERENCE" ERRORS
            implementation(compose.materialIconsExtended)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            // --- DATABASE & LOGIC (Added for Phase 1) ---

            // SQLite Driver: The engine that creates the .db file
            implementation("org.xerial:sqlite-jdbc:3.44.1.0")

            // JetBrains Exposed: The framework that lets us use Kotlin instead of raw SQL
            implementation("org.jetbrains.exposed:exposed-core:0.47.0")
            implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
            implementation("org.jetbrains.exposed:exposed-java-time:0.47.0")

            // JSON Serialization: To store complex lists (like "Services Done") inside a single DB column
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
        }
    }
}