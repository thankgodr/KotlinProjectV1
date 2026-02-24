package me.thankgodr.fintechchallegeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentDestination
import me.thankgodr.fintechchallegeapp.presentation.sendpayment.SendPaymentScreen
import me.thankgodr.fintechchallegeapp.presentation.splash.SplashDestination
import me.thankgodr.fintechchallegeapp.presentation.splash.SplashScreen
import me.thankgodr.fintechchallegeapp.presentation.success.SuccessDestination
import me.thankgodr.fintechchallegeapp.presentation.success.SuccessScreen
import me.thankgodr.fintechchallegeapp.presentation.utils.toTwoDecimalString
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
                },
                onNavigateToSuccess = { transactionId, amount, currencyCode ->
                    navController.navigate(
                        SuccessDestination(
                            transactionId = transactionId,
                            amount = amount.toTwoDecimalString(),
                            currencyCode = currencyCode
                        )
                    ) 
                }
            )
        }

        composable<SuccessDestination> { backStackEntry ->
            val dest = backStackEntry.toRoute<SuccessDestination>()
            SuccessScreen(
                transactionId = dest.transactionId,
                amount = dest.amount.toDoubleOrNull() ?: 0.0,
                currencyCode = dest.currencyCode,
                onDismiss = {
                    navController.popBackStack()
                },
                onViewHistory = {
                    navController.navigate(TransactionHistoryDestination) {
                        popUpTo(SendPaymentDestination) { inclusive = false }
                    }
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