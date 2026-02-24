package me.thankgodr.fintechchallegeapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import me.thankgodr.fintechchallegeapp.presentation.navigation.MainNavigation
import me.thankgodr.fintechchallegeapp.presentation.theme.CashiTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    CashiTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { testTagsAsResourceId = true }
        ) {
            MainNavigation()
        }
    }
}