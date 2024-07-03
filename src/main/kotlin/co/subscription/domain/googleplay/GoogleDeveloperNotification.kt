package co.subscription.domain.googleplay

import co.subscription.domain.ProductType

class DeveloperNotification(
    val packageName: String,
    val eventTimeMillis: String,
    val oneTimeProductNotification: OneTimeProductNotification?,
    val subscriptionNotification: SubscriptionNotification?,
    val testNotification: Map<Any, Any>?,
) {
    fun toGooglePlayPurchaseChangeMessage(): GooglePlayPurchaseChangeMessage =
        when {
            subscriptionNotification != null ->
                subscriptionNotification.toGooglePlayPurchaseChangeMessage()

            oneTimeProductNotification != null ->
                oneTimeProductNotification.toGooglePlayPurchaseChangeMessage()

            else -> error(
                "DeveloperNotification must have either a subscriptionNotification or oneTimeProductNotification",
            )
        }
}

data class OneTimeProductNotification(
    val version: String,
    val notificationType: Int,
    val purchaseToken: String,
    val sku: String,
) {
    fun toGooglePlayPurchaseChangeMessage() =
        GooglePlayPurchaseChangeMessage(
            productType = ProductType.NONSUBSCRIPTION,
            productId = sku,
            purchaseToken = purchaseToken,
        )
}

data class SubscriptionNotification(
    val version: String,
    val notificationType: Int,
    val purchaseToken: String,
    val subscriptionId: String,
) {
    fun toGooglePlayPurchaseChangeMessage() =
        GooglePlayPurchaseChangeMessage(
            productType = ProductType.SUBSCRIPTION,
            productId = subscriptionId,
            purchaseToken = purchaseToken,
        )
}