package com.narvane

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.narvane.components.ComponentsTesterApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Narvane Components Tester",
    ) {
        ComponentsTesterApp()
    }
}
