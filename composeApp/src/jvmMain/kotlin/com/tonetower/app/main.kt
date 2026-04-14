package com.tonetower.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    // Initialize the DB before the UI starts
    DatabaseManager.init()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Tone Tower",
    ) {
        App()
    }
}