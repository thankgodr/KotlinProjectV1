package me.thankgodr.fintechchallegeapp.presentation.transactionhistory

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

private val APP_PACKAGE = "me.thankgodr.fintechchallegeapp"
private val LAUNCH_TIMEOUT = 12_000L

@RunWith(AndroidJUnit4::class)
class TransactionHistoryScreenInstrumentedTest {
    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Launch app
        device.pressHome()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent =
            context.packageManager.getLaunchIntentForPackage(APP_PACKAGE)
                ?: throw AssertionError("App $APP_PACKAGE not found")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for splash → SendPayment screen
        device.wait(Until.hasObject(By.res("send_payment_screen")), LAUNCH_TIMEOUT)

        // Navigate to Transaction History
        val historyButton = device.wait(Until.findObject(By.res("send_payment_history_button")), LAUNCH_TIMEOUT)
        assertNotNull(historyButton, "History button should be visible")
        historyButton.click()

        // Wait for history screen
        device.wait(Until.hasObject(By.res("transaction_history_screen")), LAUNCH_TIMEOUT)
    }

    @Test
    fun historyScreenIsDisplayed() {
        val screen = device.findObject(By.res("transaction_history_screen"))
        assertNotNull(screen, "Transaction history screen should be displayed")
    }

    @Test
    fun backButtonIsDisplayed() {
        val backButton = device.findObject(By.res("transaction_history_back_button"))
        assertNotNull(backButton, "Back button should be visible")
    }

    @Test
    fun backButtonNavigatesToSendPayment() {
        val backButton = device.findObject(By.res("transaction_history_back_button"))
        assertNotNull(backButton)
        backButton.click()

        // Should return to SendPayment screen
        val sendScreen = device.wait(Until.findObject(By.res("send_payment_screen")), LAUNCH_TIMEOUT)
        assertNotNull(sendScreen, "Should navigate back to SendPayment screen")
    }

    @Test
    fun historyShowsListOrEmptyState() {
        // Either the list or empty state should be visible
        val list = device.findObject(By.res("transaction_history_list"))
        val emptyState = device.findObject(By.res("transaction_history_empty"))

        assertTrue(
            list != null || emptyState != null,
            "Either transaction list or empty state should be displayed",
        )
    }

    private fun assertTrue(
        condition: Boolean,
        message: String,
    ) {
        if (!condition) throw AssertionError(message)
    }
}
