package co.subscription.domain

interface PurchaseUpdater {
    fun update(purchase: SubscriptionPurchaseData, userInfo: PurchaseUserInfo? = null)
}