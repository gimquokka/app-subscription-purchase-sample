package co.subscription.domain.googleplay

import co.subscription.domain.ProductType

data class GooglePlayPurchaseChangeMessage(
    val productType: ProductType,
    val productId: String,
    val purchaseToken: String,
) {
    fun toProductData() =
        co.subscription.domain.ProductData(
            productId = productId,
        )
}