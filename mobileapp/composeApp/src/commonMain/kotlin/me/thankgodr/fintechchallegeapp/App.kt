package me.thankgodr.fintechchallegeapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.thankgodr.fintechchallegeapp.presentation.navigation.MainNavigation
import me.thankgodr.fintechchallegeapp.presentation.navigation.Routes
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentScreen
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentViewModel
import me.thankgodr.fintechchallegeapp.presentation.splash.SplashScreen
import me.thankgodr.fintechchallegeapp.presentation.theme.CashiTheme
import me.thankgodr.fintechchallegeapp.presentation.transactionhistory.TransactionHistoryScreen
import me.thankgodr.fintechchallegeapp.presentation.transactionhistory.TransactionHistoryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    CashiTheme {
        MainNavigation()
    }
}