package me.thankgodr.fintechchallegeapp.presentation.sendpayment

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
import kotlin.test.assertTrue

private val APP_PACKAGE = "me.thankgodr.fintechchallegeapp"
private val LAUNCH_TIMEOUT = 12_000L
@RunWith(AndroidJUnit4::class)
class SendPaymentScreenInstrumentedTest {
    private lateinit var device: UiDevice


    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Press home and launch the app
        device.pressHome()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent =
            context.packageManager.getLaunchIntentForPackage(APP_PACKAGE)
                ?: throw AssertionError("App $APP_PACKAGE not found")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for splash to finish and SendPayment screen to appear
        device.wait(Until.hasObject(By.res("send_payment_screen")), LAUNCH_TIMEOUT)
    }

    @Test
    fun screenDisplaysAllInputFields() {
        val screen = device.wait(Until.findObject(By.res("send_payment_screen")), LAUNCH_TIMEOUT)
        assertNotNull(screen, "SendPayment screen should be displayed")

        assertNotNull(device.findObject(By.res("send_payment_sender_name_input")), "Sender name input visible")
        assertNotNull(device.findObject(By.res("send_payment_email_input")), "Email input visible")
        assertNotNull(device.findObject(By.res("send_payment_amount_input")), "Amount input visible")
        assertNotNull(device.findObject(By.res("send_payment_currency_dropdown")), "Currency dropdown visible")
        assertNotNull(device.findObject(By.res("send_payment_submit_button")), "Submit button visible")
    }

    @Test
    fun submitButtonDisabledWhenFieldsEmpty() {
        val submitButton = device.wait(Until.findObject(By.res("send_payment_submit_button")), LAUNCH_TIMEOUT)
        assertNotNull(submitButton, "Submit button should exist")
        assertTrue(!submitButton.isEnabled, "Submit button should be disabled when fields are empty")
    }

    @Test
    fun fillingFieldsEnablesSubmitButton() {
        // Fill sender name
        val nameInput = device.wait(Until.findObject(By.res("send_payment_sender_name_input")), LAUNCH_TIMEOUT)
        assertNotNull(nameInput)
        nameInput.text = "Alice"

        // Fill email
        val emailInput = device.findObject(By.res("send_payment_email_input"))
        assertNotNull(emailInput)
        emailInput.text = "alice@test.com"

        // Fill amount
        val amountInput = device.findObject(By.res("send_payment_amount_input"))
        assertNotNull(amountInput)
        amountInput.text = "100"

        // Wait for recomposition
        Thread.sleep(1000)

        val submitButton = device.findObject(By.res("send_payment_submit_button"))
        assertNotNull(submitButton)
        assertTrue(submitButton.isEnabled, "Submit button should be enabled after filling all fields")
    }

    @Test
    fun historyButtonExists() {
        val historyButton = device.wait(Until.findObject(By.res("send_payment_history_button")), LAUNCH_TIMEOUT)
        assertNotNull(historyButton, "History button should be visible")
    }
}
