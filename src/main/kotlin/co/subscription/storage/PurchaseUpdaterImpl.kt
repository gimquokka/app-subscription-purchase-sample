package co.subscription.storage

import co.subscription.domain.PurchaseUpdater
import co.subscription.domain.PurchaseUserInfo
import co.subscription.domain.SubscriptionPurchaseData
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
internal class PurchaseUpdaterImpl(
    private val inAppPurchaseJpaRepository: InAppPurchaseJpaRepository,
) : PurchaseUpdater {
    @Transactional
    override fun update(
        purchase: SubscriptionPurchaseData,
        userInfo: PurchaseUserInfo?,
    ) {
        val inAppPurchaseEntity = inAppPurchaseJpaRepository.findByOrderId(purchase.orderId)
        if (inAppPurchaseEntity == null && userInfo == null) {
            return
        }
        if (inAppPurchaseEntity == null) {
            inAppPurchaseJpaRepository.save(InAppPurchaseEntity.of(userInfo!!, purchase))
            return
        }
        inAppPurchaseEntity.update(purchase)
    }
}