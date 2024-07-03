package co.subscription.domain

data class ProductData(
    val productId: String,
) {
    val type: ProductType =
        mapOf("your-product-id" to ProductType.SUBSCRIPTION)[productId]
            ?: throw IllegalArgumentException("Unknown product id: $productId")

    val isSubscription = (type == ProductType.SUBSCRIPTION)
    val isNonSubscription = (type == ProductType.NONSUBSCRIPTION)
}

enum class ProductType {
    SUBSCRIPTION,
    NONSUBSCRIPTION,
}