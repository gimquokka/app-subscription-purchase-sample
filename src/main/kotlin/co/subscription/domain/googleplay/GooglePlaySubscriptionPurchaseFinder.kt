package co.subscription.domain.googleplay

import co.subscription.domain.SubscriptionPurchaseData

interface GooglePlaySubscriptionPurchaseFinder {
    fun find(
        userId: String,
        productId: String,
    ): SubscriptionPurchaseData?
}