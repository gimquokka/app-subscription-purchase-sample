package co.subscription.storage

import co.subscription.domain.SubscriptionPurchaseData
import co.subscription.domain.googleplay.GooglePlaySubscriptionPurchaseFinder
import org.springframework.stereotype.Component

@Component
internal class GooglePlaySubscriptionPurchaseFinderImpl(
    private val inAppPurchaseJpaRepository: InAppPurchaseJpaRepository,
) : GooglePlaySubscriptionPurchaseFinder {
    override fun find(
        userId: String,
        productId: String,
    ): SubscriptionPurchaseData? = inAppPurchaseJpaRepository.findLatestActive(userId, productId)?.toSubscriptionPurchase()
}