package co.subscription.domain

import java.time.LocalDate
import java.time.ZonedDateTime

abstract class Purchase(
    val inAppPurchaseSource: InAppPurchaseSource,
    val orderId: String,
    val productId: String,
    val purchaseDate: ZonedDateTime,
    val type: ProductType,
)

/**
 *  SubscriptionPurchase라는 Class명이 이미 구글플레이 라이브러리에서 사용되고 있어서
 *  SubscriptionPurchaseData로 변경하였습니다.
 */
class SubscriptionPurchaseData(
    inAppPurchaseSource: InAppPurchaseSource,
    orderId: String,
    productId: String,
    purchaseAt: ZonedDateTime,
    type: ProductType,
    val subscriptionPaymentStatus: PaymentState?,
    val expiryAt: ZonedDateTime,
    val autoRenewing: Boolean,
    val cancelReason: Int? = null,
    val userCancellationAt: ZonedDateTime? = null,
    val autoResumeAt: ZonedDateTime?,
) : Purchase(
        inAppPurchaseSource = inAppPurchaseSource,
        orderId = orderId,
        productId = productId,
        purchaseDate = purchaseAt,
        type = type,
    ) {
    val isAvailable: Boolean
        get() = expiryAt > ZonedDateTime.now()

    val estimatedPaymentAt: ZonedDateTime?
        get() {
            val isCancelled = userCancellationAt != null || cancelReason != null
            if (isCancelled) {
                return null
            }

            return expiryAt
        }

    val endAt: ZonedDateTime?
        get() {
            val isNotResumed = autoResumeAt != null && autoResumeAt > ZonedDateTime.now()
            if (isNotResumed) {
                return null
            }

            return expiryAt
        }

    val isRefund: Boolean
        get() = cancelReason == 1
}

data class SubscriptionPurchaseInfo(
    val isAvailable: Boolean,
    val isPurchased: Boolean,
    val isAutoRenewing: Boolean? = null,
    val isPurchaseFailed: Boolean? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val estimatedPaymentDate: LocalDate? = null,
    val cancellationDate: LocalDate? = null,
    val autoResumeDate: LocalDate? = null,
) {
    companion object {
        fun from(subscriptionPurchase: SubscriptionPurchaseData?): SubscriptionPurchaseInfo {
            if (subscriptionPurchase == null || subscriptionPurchase.isRefund) {
                return SubscriptionPurchaseInfo(
                    isAvailable = false,
                    isPurchased = false,
                )
            }

            return with(subscriptionPurchase) {
                SubscriptionPurchaseInfo(
                    isAvailable = isAvailable,
                    isPurchased = true,
                    isAutoRenewing = autoRenewing,
                    isPurchaseFailed = subscriptionPaymentStatus == PaymentState.PENDING,
                    startDate = purchaseDate.toLocalDate(),
                    endDate = endAt?.toLocalDate(),
                    estimatedPaymentDate = estimatedPaymentAt?.toLocalDate(),
                    cancellationDate = userCancellationAt?.toLocalDate(),
                    autoResumeDate = autoResumeAt?.toLocalDate(),
                )
            }
        }
    }
}

enum class InAppPurchaseSource {
    GOOGLE_PLAY,
    APPLE_APP_STORE,
    ;

    val isGooglePlay: Boolean by lazy { (this == GOOGLE_PLAY) }
    val isAppStore: Boolean by lazy { (this == APPLE_APP_STORE) }
}

enum class PaymentState {
    PENDING,
    RECEIVED,
    FREE_TRIAL,
    PENDING_DEFERRED_UPGRADE_DOWNGRADE,
}

data class PurchaseUserInfo(
    val deviceUuid: String,
    val userId: String? = null,
) {
    val source: InAppPurchaseSource =
        if (deviceUuid.length < 30) {
            InAppPurchaseSource.GOOGLE_PLAY
        } else {
            InAppPurchaseSource.APPLE_APP_STORE
        }
}