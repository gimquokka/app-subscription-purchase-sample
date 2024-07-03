package co.subscription.storage

import co.subscription.domain.InAppPurchaseSource
import co.subscription.domain.PaymentState
import co.subscription.domain.PurchaseUserInfo
import co.subscription.domain.SubscriptionPurchaseData
import co.subscription.storage.base.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime

@Entity
@Table(name = "in_app_purchase")
@DynamicUpdate
class InAppPurchaseEntity(
    @Column(length = 16)
    @Enumerated(EnumType.STRING)
    val inAppPurchaseSource: InAppPurchaseSource,
    @Column(columnDefinition = "VARCHAR(256) NOT NULL")
    val orderId: String,
    @Column(columnDefinition = "VARCHAR(64) NOT NULL")
    val productId: String,
    @Column(columnDefinition = "VARCHAR(64) NOT NULL")
    val deviceUuid: String,
    @Column(columnDefinition = "VARCHAR(64)")
    var userId: String? = null,
    @Column(columnDefinition = "DATETIME NOT NULL")
    var purchaseAt: ZonedDateTime,
    @Column(columnDefinition = "VARCHAR(32) NOT NULL")
    @Enumerated(EnumType.STRING)
    val productType: co.subscription.domain.ProductType,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)")
    var subscriptionPaymentStatus: PaymentState? = null,
    @Column(columnDefinition = "DATETIME")
    var subscriptionExpiryAt: ZonedDateTime? = null,
    var subscriptionAutoRenewing: Boolean,
    var subscriptionCancelReason: Int?,
    var subscriptionUserCancellationAt: ZonedDateTime?,
    var subscriptionAutoResumeAt: ZonedDateTime?,
    id: Long = 0L,
) : BaseEntity(id) {
    @Version
    @UpdateTimestamp
    @Column(nullable = false)
    var version: ZonedDateTime? = null

    fun update(purchase: SubscriptionPurchaseData) {
        purchaseAt = purchase.purchaseDate
        subscriptionPaymentStatus = purchase.subscriptionPaymentStatus ?: subscriptionPaymentStatus
        subscriptionExpiryAt = purchase.expiryAt
        subscriptionAutoRenewing = purchase.autoRenewing
        subscriptionCancelReason = purchase.cancelReason
        subscriptionUserCancellationAt = purchase.userCancellationAt
        subscriptionAutoResumeAt = purchase.autoResumeAt
    }

    fun toSubscriptionPurchase(): SubscriptionPurchaseData {
        val subscriptionPurchase =
            SubscriptionPurchaseData(
                inAppPurchaseSource = inAppPurchaseSource,
                orderId = orderId,
                productId = productId,
                purchaseAt = purchaseAt,
                type = productType,
                subscriptionPaymentStatus = subscriptionPaymentStatus!!,
                expiryAt = subscriptionExpiryAt!!,
                autoRenewing = subscriptionAutoRenewing,
                cancelReason = subscriptionCancelReason,
                userCancellationAt = subscriptionUserCancellationAt,
                autoResumeAt = subscriptionAutoResumeAt,
            )

        return subscriptionPurchase
    }

    companion object {
        fun of(
            userInfo: PurchaseUserInfo,
            subscriptionPurchase: SubscriptionPurchaseData,
        ): InAppPurchaseEntity =
            with(subscriptionPurchase) {
                InAppPurchaseEntity(
                    inAppPurchaseSource = inAppPurchaseSource,
                    orderId = orderId,
                    productId = productId,
                    deviceUuid = userInfo.deviceUuid,
                    userId = userInfo.userId,
                    purchaseAt = purchaseDate,
                    productType = type,
                    subscriptionPaymentStatus = subscriptionPaymentStatus,
                    subscriptionExpiryAt = expiryAt,
                    subscriptionAutoRenewing = autoRenewing,
                    subscriptionCancelReason = cancelReason,
                    subscriptionUserCancellationAt = userCancellationAt,
                    subscriptionAutoResumeAt = autoResumeAt,
                )
            }
    }
}