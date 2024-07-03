package co.subscription.domain.googleplay

import co.subscription.domain.PaymentState
import org.springframework.stereotype.Component

@Component
internal class GooglePlayPurchaseHandler(
    private val purchaseClient: GooglePlayPurchaseClient,
) {
    /**
     * Handle non-subscription purchases (one time purchases).
     * Retrieves the purchase status from Google Play.
     * 단건 결제는 구현하지 않았습니다.
     */
    fun handleNonSubscription(
        productData: co.subscription.domain.ProductData,
        token: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Handle subscription purchases.
     * Retrieves the purchase status from Google Play.
     */
    fun handleSubscription(
        productData: co.subscription.domain.ProductData,
        token: String,
    ): Boolean {
        val purchase = purchaseClient.findSubscriptionPurchase(productData.productId, token)

        val isUnpaid = (purchase == null)
        val isPaymentPending = (purchase?.subscriptionPaymentStatus == PaymentState.PENDING)
        return (isUnpaid || isPaymentPending).not()
    }
}