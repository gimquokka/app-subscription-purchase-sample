package co.subscription.domain

import co.subscription.domain.googleplay.GooglePlayPurchaseClient
import org.springframework.stereotype.Component

@Component
class PurchaseChangeUpdater(
    private val googlePlayPurchaseReader: GooglePlayPurchaseClient,
    private val purchaseUpdater: PurchaseUpdater,
) {
    fun update(
        productData: ProductData,
        token: String,
        purchaseUserInfo: PurchaseUserInfo? = null,
    ) {
        val purchase =
            googlePlayPurchaseReader.findSubscriptionPurchase(productData.productId, token)
                ?: return

        purchaseUpdater.update(purchase, purchaseUserInfo)
    }
}