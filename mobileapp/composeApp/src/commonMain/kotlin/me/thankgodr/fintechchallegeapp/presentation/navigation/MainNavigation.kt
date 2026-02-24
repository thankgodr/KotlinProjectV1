package me.thankgodr.fintechchallegeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentDestination
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentScreen
import me.thankgodr.fintechchallegeapp.presentation.splash.SplashDestination
import me.thankgodr.fintechchallegeapp.presentation.splash.SplashScreen
import me.thankgodr.fintechchallegeapp.presentation.transactionhistory.TransactionHistoryDestination
import me.thankgodr.fintechchallegeapp.presentation.transactionhistory.TransactionHistoryScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SplashDestination
    ) {
        composable<SplashDestination>{
            SplashScreen{
                navController.navigate(SendPaymentDestination) {
                    popUpTo(SplashDestination) { inclusive = true }
                }
            }
        }

        composable<SendPaymentDestination> {
            SendPaymentScreen(
                onNavigateToHistory = {
                    navController.navigate(TransactionHistoryDestination)
                }
            )
        }

        composable<TransactionHistoryDestination> {
            TransactionHistoryScreen{
                navController.popBackStack()
            }
        }
    }
}