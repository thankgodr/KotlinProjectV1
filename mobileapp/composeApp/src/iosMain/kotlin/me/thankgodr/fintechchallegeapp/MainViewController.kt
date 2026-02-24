package me.thankgodr.fintechchallegeapp

import androidx.compose.ui.window.ComposeUIViewController
import me.thankgodr.fintechchallegeapp.di.initKoin

fun MainViewController() = ComposeUIViewController { App() }

fun initKoinIOS() {
    initKoin()
}
