package co.subscription.domain.appleappstore

import co.subscription.domain.InAppPurchaseSource
import co.subscription.domain.PaymentState
import co.subscription.domain.ProductType
import co.subscription.domain.SubscriptionPurchaseData
import co.subscription.support.toDecodedPayload
import co.subscription.support.toZonedDateTime
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.ZonedDateTime

class AppleAppStoreServerNotification(
    data: Data,
    notificationType: String,
) {
    /** cf)
     * notificationUUID: String,
     * signedDate: Long,
     * subtype: String,
     * version: String,
     */
    private val subscriptionPaymentStatus: PaymentState =
        when (notificationType) {
            "DID_FAIL_TO_RENEW" -> PaymentState.PENDING
            else -> PaymentState.RECEIVED
        }
    private val renewalInfo: RenewalInfo =
        data.signedRenewalInfo.toDecodedPayload().run {
            ObjectMapper().readValue(this)
        }
    private val transactionInfo: TransactionInfo =
        data.signedTransactionInfo.toDecodedPayload().run {
            ObjectMapper().readValue(this)
        }

    private val expiryAt: ZonedDateTime = transactionInfo.expiresDate.toZonedDateTime()
    private val autoRenewing: Boolean = renewalInfo.autoRenewStatus == 1
    private val autoResumeAt: ZonedDateTime? = if (autoRenewing) expiryAt else null

    fun toSubscriptionPurchase() =
        SubscriptionPurchaseData(
            inAppPurchaseSource = InAppPurchaseSource.APPLE_APP_STORE,
            orderId = transactionInfo.originalTransactionId,
            productId = transactionInfo.productId,
            purchaseAt = transactionInfo.purchaseAt,
            type = ProductType.SUBSCRIPTION,
            subscriptionPaymentStatus = subscriptionPaymentStatus,
            expiryAt = expiryAt,
            autoRenewing = autoRenewing,
            autoResumeAt = autoResumeAt,
        )

    class Data(
        val appAppleId: Int,
        val bundleId: String,
        val bundleVersion: String,
        val environment: String,
        val signedRenewalInfo: String,
        val signedTransactionInfo: String,
        val status: Int,
    )

    companion object {
        fun from(jws: String): AppleAppStoreServerNotification =
            jws.toDecodedPayload().run {
                ObjectMapper().readValue(this)
            }
    }
}

data class TransactionInfo(
    val bundleId: String,
    val environment: String,
    val expiresDate: Long,
    val inAppOwnershipType: String,
    val originalPurchaseDate: Long,
    val originalTransactionId: String,
    val productId: String,
    val purchaseDate: Long,
    val quantity: Int,
    val signedDate: Long,
    val storefront: String,
    val storefrontId: String,
    val subscriptionGroupIdentifier: String,
    val transactionId: String,
    val transactionReason: String,
    val type: String,
    val webOrderLineItemId: String,
) {
    val purchaseAt = purchaseDate.toZonedDateTime()
}

data class RenewalInfo(
    val autoRenewProductId: String,
    val autoRenewStatus: Int,
    val environment: String,
    val isInBillingRetryPeriod: Boolean,
    val originalTransactionId: String,
    val productId: String,
    val recentSubscriptionStartDate: Long,
    val renewalDate: Long,
    val signedDate: Long,
)