package me.thankgodr.fintechchallegeapp.presentation.utils

/**
 * Centralized test tag constants for Appium / UI testing.
 * Usage: Modifier.testTag(TestTags.SendPayment.EMAIL_INPUT)
 */
object TestTags {

    object Splash {
        const val SCREEN = "splash_screen"
        const val LOGO = "splash_logo"
        const val APP_NAME = "splash_app_name"
        const val TAGLINE = "splash_tagline"
    }

    object SendPayment {
        const val SCREEN = "send_payment_screen"
        const val AMOUNT_DISPLAY = "send_payment_amount_display"
        const val SENDER_NAME_INPUT = "send_payment_sender_name_input"
        const val EMAIL_INPUT = "send_payment_email_input"
        const val AMOUNT_INPUT = "send_payment_amount_input"
        const val CURRENCY_DROPDOWN = "send_payment_currency_dropdown"
        const val CURRENCY_MENU = "send_payment_currency_menu"
        const val SUBMIT_BUTTON = "send_payment_submit_button"
        const val LOADING_INDICATOR = "send_payment_loading"
        const val ERROR_CARD = "send_payment_error_card"
        const val HISTORY_BUTTON = "send_payment_history_button"
        fun currencyOption(code: String) = "send_payment_currency_$code"
    }

    object Success {
        const val OVERLAY = "success_overlay"
        const val ICON = "success_icon"
        const val TITLE = "success_title"
        const val AMOUNT = "success_amount"
        const val TRANSACTION_ID = "success_transaction_id"
        const val VIEW_HISTORY_BUTTON = "success_view_history_button"
        const val SEND_ANOTHER_BUTTON = "success_send_another_button"
    }

    object TransactionHistory {
        const val SCREEN = "transaction_history_screen"
        const val BACK_BUTTON = "transaction_history_back_button"
        const val LOADING_INDICATOR = "transaction_history_loading"
        const val ERROR_TEXT = "transaction_history_error"
        const val EMPTY_STATE = "transaction_history_empty"
        const val LIST = "transaction_history_list"
        fun transactionItem(id: String) = "transaction_item_$id"
        fun transactionAmount(id: String) = "transaction_amount_$id"
        fun transactionStatus(id: String) = "transaction_status_$id"
    }
}
