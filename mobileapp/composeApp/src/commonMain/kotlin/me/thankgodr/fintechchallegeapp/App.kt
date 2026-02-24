package me.thankgodr.fintechchallegeapp

import androidx.compose.runtime.Composable
import me.thankgodr.fintechchallegeapp.presentation.navigation.MainNavigation
import me.thankgodr.fintechchallegeapp.presentation.theme.CashiTheme

@Composable
fun App() {
    CashiTheme {
        MainNavigation()
    }
}