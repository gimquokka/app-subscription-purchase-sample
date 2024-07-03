package co.subscription.domain.googleplay

import co.subscription.domain.Purchase
import co.subscription.domain.SubscriptionPurchaseData

interface GooglePlayPurchaseClient {
    fun findSubscriptionPurchase(
        productId: String,
        token: String,
    ): SubscriptionPurchaseData?

    fun findNonSubscriptionPurchase(
        productId: String,
        token: String,
    ): Purchase?
}