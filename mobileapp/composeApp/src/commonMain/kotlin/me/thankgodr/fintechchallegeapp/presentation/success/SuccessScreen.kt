package me.thankgodr.fintechchallegeapp.presentation.success

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.woowla.compose.icon.collections.fontawesome.FontAwesome
import com.woowla.compose.icon.collections.fontawesome.fontawesome.Regular
import com.woowla.compose.icon.collections.fontawesome.fontawesome.regular.CircleCheck
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.a11y_success_checkmark
import kotlinproject.composeapp.generated.resources.success_payment_sent
import kotlinproject.composeapp.generated.resources.success_send_another
import kotlinproject.composeapp.generated.resources.success_transaction_id_prefix
import kotlinproject.composeapp.generated.resources.success_view_history
import kotlinx.serialization.Serializable
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.presentation.utils.TestTags
import me.thankgodr.fintechchallegeapp.presentation.utils.toTwoDecimalString
import org.jetbrains.compose.resources.stringResource

@Serializable
data class SuccessDestination(
    val transactionId: String,
    val amount: String,
    val currencyCode: String
)

@Composable
fun SuccessScreen(
    transactionId: String,
    amount: Double,
    currencyCode: String,
    onDismiss: () -> Unit,
    onViewHistory: () -> Unit
) {
    val currency = Currency.fromCode(currencyCode) ?: Currency.USD

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
