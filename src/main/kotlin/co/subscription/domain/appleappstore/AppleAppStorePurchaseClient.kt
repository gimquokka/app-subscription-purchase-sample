package co.subscription.domain.appleappstore

import co.subscription.domain.Purchase
import co.subscription.domain.SubscriptionPurchaseData

interface AppleAppStorePurchaseClient {
    fun findSubscriptionPurchase(
        productId: String,
        token: String,
    ): SubscriptionPurchaseData?

    fun findNonSubscriptionPurchase(
        productId: String,
        token: String,
    ): Purchase?
}