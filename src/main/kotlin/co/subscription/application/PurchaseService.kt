package co.subscription.application

import co.subscription.domain.*
import co.subscription.domain.appleappstore.AppleAppStoreServerNotificationTokenParser
import co.subscription.domain.googleplay.GooglePlayPurchaseChangeSubscriberClient
import co.subscription.domain.googleplay.GooglePlaySubscriptionPurchaseFinder
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PurchaseService(
    private val purchaseClient: PurchaseClient,
    private val purchaseUpdater: PurchaseUpdater,
    private val purchaseChangeUpdater: PurchaseChangeUpdater,
    private val appleAppStoreServerNotificationTokenParser: AppleAppStoreServerNotificationTokenParser,
    private val googlePlayPurchaseChangeSubscriber: GooglePlayPurchaseChangeSubscriberClient,
    private val purchaseFinder: GooglePlaySubscriptionPurchaseFinder,
) {
    @Transactional
    fun verifyPurchase(
        userInfo: PurchaseUserInfo,
        productData: ProductData,
        token: String,
    ): Boolean {
        val purchase =
            purchaseClient.fetch(userInfo.source, productData, token)
                ?: return false

        purchaseUpdater.update(purchase as SubscriptionPurchaseData, userInfo)
        return true
    }

    /**
     * google play api를 활용하여
     * 구독 갱신, 일시중지, 해지 등의 변경사항을 확인하고 업데이트합니다.
     */
    fun updateGooglePlayPurchaseChange() {
        val (ackIds, messages) = googlePlayPurchaseChangeSubscriber.pull()

        for (message in messages) {
            // TODO: PurchaseChangeUpdater 클래스 제거
            purchaseChangeUpdater.update(message.toProductData(), message.purchaseToken)
        }

        googlePlayPurchaseChangeSubscriber.acknowledge(ackIds)
    }

    /**
     * App Store Server Notifications의 Webhook을 활용하여
     * Apple App Store의 환불, 결제수단 만료 등 변경 사항을 업데이트 합니다.
     */
    fun updateAppStorePurchaseChange(jws: String) {
        /** note
         * 1. token을 PurchaseSubscription로 변환
         * 2. PurchaseUpdater를 활용하여 PurchaseSubscription을 업데이트
         */
        val subscriptionPurchase = appleAppStoreServerNotificationTokenParser.parse(jws)
        purchaseUpdater.update(subscriptionPurchase)
    }

    fun readSubscription(
        userId: String,
        productId: String,
    ): SubscriptionPurchaseInfo =
        purchaseFinder.find(userId, productId).run {
            SubscriptionPurchaseInfo.from(this)
        }
}