package co.subscription.domain

import co.subscription.domain.appleappstore.AppleAppStorePurchaseClient
import co.subscription.domain.googleplay.GooglePlayPurchaseClient
import org.springframework.stereotype.Component

@Component
class PurchaseClient(
    private val googlePlayPurchaseClient: GooglePlayPurchaseClient,
    private val appleAppStorePurchaseClient: AppleAppStorePurchaseClient,
) {
    fun fetch(
        source: InAppPurchaseSource,
        productData: ProductData,
        token: String,
    ): Purchase? =
        when {
            source.isGooglePlay && productData.isSubscription ->
                googlePlayPurchaseClient.findSubscriptionPurchase(productData.productId, token)

            source.isGooglePlay && productData.isNonSubscription ->
                googlePlayPurchaseClient.findNonSubscriptionPurchase(productData.productId, token)

            source.isAppStore && productData.isSubscription ->
                appleAppStorePurchaseClient.findSubscriptionPurchase(productData.productId, token)

            source.isAppStore && productData.isNonSubscription ->
                appleAppStorePurchaseClient.findNonSubscriptionPurchase(productData.productId, token)

            else -> throw IllegalArgumentException("Unknown purchase source or type: $source, ${productData.type}")
        }
}