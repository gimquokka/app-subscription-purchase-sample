package co.subscription.client

import co.subscription.config.GooglePlaySecretsManager
import co.subscription.domain.*
import co.subscription.domain.googleplay.GooglePlayPurchaseClient
import co.subscription.support.toZonedDateTime
import com.google.api.services.androidpublisher.AndroidPublisher
import org.springframework.stereotype.Component
import com.google.api.services.androidpublisher.model.SubscriptionPurchase as GooglePlaySubscriptionPurchaseResponse

@Component
internal class GooglePlayPurchaseClientImpl(
    private val googlePlaySecretsManager: GooglePlaySecretsManager,
) : GooglePlayPurchaseClient {
    private val androidPublisher: AndroidPublisher
        get() = TODO("실제 서비스에서는 service account를 발급받아 사용하세요.")

    override fun findSubscriptionPurchase(
        productId: String,
        token: String,
    ): SubscriptionPurchaseData? {
        val response: GooglePlaySubscriptionPurchaseResponse =
            try {
                androidPublisher
                    .Purchases()
                    .subscriptions()[googlePlaySecretsManager.applicationPackageName, productId, token]
                    .execute()
            } catch (e: Exception) {
                throw IllegalStateException(
                    "Failed to find subscription purchase. productId: $productId, token: $token",
                    e,
                )
            }

        if (response.orderId == null) return null

        return response.toSubscriptionPurchase(productId)
    }

    private fun GooglePlaySubscriptionPurchaseResponse.toSubscriptionPurchase(productId: String) =
        SubscriptionPurchaseData(
            inAppPurchaseSource = InAppPurchaseSource.GOOGLE_PLAY,
            orderId = extractOrderId(orderId),
            productId = productId,
            purchaseAt = startTimeMillis.toZonedDateTime(),
            type = ProductType.SUBSCRIPTION,
            subscriptionPaymentStatus = paymentState?.let { subscriptionStatusFrom(paymentState) },
            expiryAt = expiryTimeMillis.toZonedDateTime(),
            autoRenewing = autoRenewing,
            cancelReason = cancelReason,
            userCancellationAt = userCancellationTimeMillis?.toZonedDateTime(),
            autoResumeAt = autoResumeTimeMillis?.toZonedDateTime(),
        )

    private fun subscriptionStatusFrom(state: Int): PaymentState =
        when (state) {
            0 -> PaymentState.PENDING
            1 -> PaymentState.RECEIVED
            2 -> PaymentState.FREE_TRIAL
            3 -> PaymentState.PENDING_DEFERRED_UPGRADE_DOWNGRADE
            else -> throw IllegalStateException("Unknown payment state: $state")
        }

    private fun extractOrderId(orderId: String): String = orderId.split("..").firstOrNull() ?: orderId

    override fun findNonSubscriptionPurchase(
        productId: String,
        token: String,
    ): Purchase? {
        TODO("Not yet implemented")
    }
}