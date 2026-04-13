package com.tonetower.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    // 1. Initialize the DB before anything else
    DatabaseManager.init()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Tone Tower",
    ) {
        App()
    }
}