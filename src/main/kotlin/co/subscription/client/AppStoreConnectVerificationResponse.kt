package co.subscription.client

import co.subscription.domain.InAppPurchaseSource
import co.subscription.domain.PaymentState
import co.subscription.domain.ProductType
import co.subscription.domain.SubscriptionPurchaseData
import co.subscription.support.toZonedDateTime
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

class AppStoreConnectVerificationResponse(
    status: Int,
    @JsonProperty("latest_receipt_info")
    private val receiptInfos: List<ReceiptInfoDto>?,
    @JsonProperty("pending_renewal_info")
    pendingRenewalInfos: List<PendingRenewalInfo>?,
) {
    val isValid: Boolean = (status == 0)
    val isSandBoxEnvironment: Boolean = (status == 21007)

    private val lastReceiptInfo: ReceiptInfoDto
        get() =
            receiptInfos?.maxBy { it.expiryAt }
                ?: throw IllegalStateException("결제 정보가 존재하지 않습니다.")
    private val lastPendingRenewalInfo: PendingRenewalInfo? = pendingRenewalInfos?.firstOrNull()

    fun toSubscriptionPurchase(): SubscriptionPurchaseData {
        with(lastReceiptInfo) {
            val autoResumeAt =
                if (lastPendingRenewalInfo?.autoRenewing == true) {
                    expiryAt
                } else {
                    null
                }

            return SubscriptionPurchaseData(
                productId = productId,
                inAppPurchaseSource = InAppPurchaseSource.APPLE_APP_STORE,
                orderId = originalTransactionId,
                // 최초의 구독 시작일
                purchaseAt = originalPurchaseAt,
                type = ProductType.SUBSCRIPTION,
                subscriptionPaymentStatus = PaymentState.RECEIVED,
                expiryAt = expiryAt,
                autoRenewing = lastPendingRenewalInfo?.autoRenewing ?: false,
                autoResumeAt = autoResumeAt,
            )
        }
    }
}

class ReceiptInfoDto(
    @JsonProperty("product_id")
    val productId: String,
    @JsonProperty("original_transaction_id")
    val originalTransactionId: String,
    @JsonProperty("original_purchase_date_ms")
    originalPurchaseDateMs: Long,
    @JsonProperty("expires_date_ms")
    expiresDateMs: Long,
) {
    val originalPurchaseAt: ZonedDateTime = originalPurchaseDateMs.toZonedDateTime()
    val expiryAt: ZonedDateTime = expiresDateMs.toZonedDateTime()
}

class PendingRenewalInfo(
    @JsonProperty("auto_renew_status")
    autoRenewStatus: Int,
) {
    val autoRenewing: Boolean = (autoRenewStatus == 1)
}