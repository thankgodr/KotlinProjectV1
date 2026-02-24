package me.thankgodr.fintechchallegeapp.presentation.sendpayment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.woowla.compose.icon.collections.fontawesome.FontAwesome
import com.woowla.compose.icon.collections.fontawesome.fontawesome.Regular
import com.woowla.compose.icon.collections.fontawesome.fontawesome.Solid
import com.woowla.compose.icon.collections.fontawesome.fontawesome.regular.CircleCheck
import com.woowla.compose.icon.collections.fontawesome.fontawesome.regular.User
import com.woowla.compose.icon.collections.fontawesome.fontawesome.solid.ArrowRight
import com.woowla.compose.icon.collections.fontawesome.fontawesome.solid.Envelope
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.a11y_email_icon
import kotlinproject.composeapp.generated.resources.a11y_navigate_to_history
import kotlinproject.composeapp.generated.resources.a11y_sender_name_icon
import kotlinproject.composeapp.generated.resources.a11y_success_checkmark
import kotlinproject.composeapp.generated.resources.send_payment_button_submit
import kotlinproject.composeapp.generated.resources.send_payment_default_amount
import kotlinproject.composeapp.generated.resources.send_payment_history
import kotlinproject.composeapp.generated.resources.send_payment_label_amount
import kotlinproject.composeapp.generated.resources.send_payment_label_currency
import kotlinproject.composeapp.generated.resources.send_payment_label_recipient_email
import kotlinproject.composeapp.generated.resources.send_payment_label_your_name
import kotlinproject.composeapp.generated.resources.send_payment_title
import kotlinproject.composeapp.generated.resources.success_payment_sent
import kotlinproject.composeapp.generated.resources.success_send_another
import kotlinproject.composeapp.generated.resources.success_transaction_id_prefix
import kotlinproject.composeapp.generated.resources.success_view_history
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.Currency.Companion.EUR
import me.thankgodr.fintechchallegeapp.domain.model.Currency.Companion.GBP
import me.thankgodr.fintechchallegeapp.domain.model.Currency.Companion.GHS
import me.thankgodr.fintechchallegeapp.domain.model.Currency.Companion.NGN
import me.thankgodr.fintechchallegeapp.domain.model.Currency.Companion.USD
import me.thankgodr.fintechchallegeapp.presentation.utils.TestTags
import me.thankgodr.fintechchallegeapp.presentation.utils.toTwoDecimalString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPaymentScreen(
    viewModel: SendPaymentViewModel = koinViewModel(),
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var currencyExpanded by remember { mutableStateOf(false) }

    val successTx = state.successTransaction
    if (successTx != null) {
        SuccessOverlay(
            transactionId = successTx.id,
            amount = successTx.amount,
            currency = successTx.currency,
            onDismiss = { viewModel.onIntent(SendPaymentIntent.ResetForm) },
            onViewHistory = {
                viewModel.onIntent(SendPaymentIntent.ResetForm)
                onNavigateToHistory()
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.testTag(TestTags.SendPayment.SCREEN),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.send_payment_title), fontWeight = FontWeight.SemiBold)
                },
                actions = {
                    TextButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag(TestTags.SendPayment.HISTORY_BUTTON)
                    ) {
                        Text(stringResource(Res.string.send_payment_history))
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            FontAwesome.Solid.ArrowRight,
                            contentDescription = stringResource(Res.string.a11y_navigate_to_history),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.SendPayment.AMOUNT_DISPLAY),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${state.selectedCurrency.currencySymbol}${state.amount.ifEmpty { stringResource(Res.string.send_payment_default_amount) }}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = state.selectedCurrency.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.senderName,
                onValueChange = { viewModel.onIntent(SendPaymentIntent.UpdateSenderName(it)) },
                label = { Text(stringResource(Res.string.send_payment_label_your_name)) },
                leadingIcon = { Icon(FontAwesome.Regular.User, contentDescription = stringResource(Res.string.a11y_sender_name_icon), modifier = Modifier.size(16.dp)) },
                isError = state.senderNameError != null,
                supportingText = state.senderNameError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth().testTag(TestTags.SendPayment.SENDER_NAME_INPUT),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = state.recipientEmail,
                onValueChange = { viewModel.onIntent(SendPaymentIntent.UpdateRecipientEmail(it)) },
                label = { Text(stringResource(Res.string.send_payment_label_recipient_email)) },
                leadingIcon = { Icon(FontAwesome.Solid.Envelope, contentDescription = stringResource(Res.string.a11y_email_icon), modifier = Modifier.size(16.dp)) },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth().testTag(TestTags.SendPayment.EMAIL_INPUT),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        viewModel.onIntent(SendPaymentIntent.UpdateAmount(newValue))
                    }
                },
                label = { Text(stringResource(Res.string.send_payment_label_amount)) },
                leadingIcon = {
                    Text(
                        state.selectedCurrency.currencySymbol,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.onIntent(SendPaymentIntent.SubmitPayment)
                    }
                ),
                modifier = Modifier.fillMaxWidth().testTag(TestTags.SendPayment.AMOUNT_INPUT),
                shape = RoundedCornerShape(12.dp)
            )

            Box(modifier = Modifier.testTag(TestTags.SendPayment.CURRENCY_DROPDOWN)) {
                OutlinedTextField(
                    value = "${state.selectedCurrency.flagEmoji} ${state.selectedCurrency.currencyCode}",
                    onValueChange = {},
                    label = { Text(stringResource(Res.string.send_payment_label_currency)) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                // Transparent overlay to capture clicks
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { currencyExpanded = true }
                )
                DropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false },
                    modifier = Modifier.testTag(TestTags.SendPayment.CURRENCY_MENU)
                ) {
                    listOf(NGN,GHS, USD,  EUR, GBP).forEach { currency ->
                        DropdownMenuItem(
                            text = {
                                Text("${currency.flagEmoji} ${currency.currencyCode} - ${currency.name}")
                            },
                            onClick = {
                                viewModel.onIntent(SendPaymentIntent.SelectCurrency(currency))
                                currencyExpanded = false
                            },
                            modifier = Modifier.testTag(TestTags.SendPayment.currencyOption(currency.currencyCode))
                        )
                    }
                }
            }

            if (state.generalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.SendPayment.ERROR_CARD),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = state.generalError.orEmpty(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onIntent(SendPaymentIntent.SubmitPayment) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag(TestTags.SendPayment.SUBMIT_BUTTON),
                enabled = state.isFormValid && !state.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).testTag(TestTags.SendPayment.LOADING_INDICATOR),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        stringResource(Res.string.send_payment_button_submit),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SuccessOverlay(
    transactionId: String,
    amount: Double,
    currency: Currency,
    onDismiss: () -> Unit,
    onViewHistory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .testTag(TestTags.Success.OVERLAY),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    FontAwesome.Regular.CircleCheck,
                    contentDescription = stringResource(Res.string.a11y_success_checkmark),
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(80.dp).testTag(TestTags.Success.ICON)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    stringResource(Res.string.success_payment_sent),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag(TestTags.Success.TITLE)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${currency.currencySymbol}${amount.toTwoDecimalString()} ${currency.currencyCode}",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.testTag(TestTags.Success.AMOUNT)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${stringResource(Res.string.success_transaction_id_prefix)} ${transactionId.take(8)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag(TestTags.Success.TRANSACTION_ID)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onViewHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag(TestTags.Success.VIEW_HISTORY_BUTTON),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(Res.string.success_view_history))
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag(TestTags.Success.SEND_ANOTHER_BUTTON)
                ) {
                    Text(stringResource(Res.string.success_send_another))
                }
            }
        }
    }
}
