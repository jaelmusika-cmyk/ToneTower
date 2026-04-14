plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm {
        // This explicitly tells the JVM target where to find the entry point
        withJava()
        mainRun {
            mainClass = "com.tonetower.app.MainKt"
        }
    }

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
            implementation(compose.materialIconsExtended)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("org.xerial:sqlite-jdbc:3.44.1.0")
            implementation("org.jetbrains.exposed:exposed-core:0.47.0")
            implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
            implementation("org.jetbrains.exposed:exposed-java-time:0.47.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.tonetower.app.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "ToneTower"
            packageVersion = "1.0.0"
        }
    }
}