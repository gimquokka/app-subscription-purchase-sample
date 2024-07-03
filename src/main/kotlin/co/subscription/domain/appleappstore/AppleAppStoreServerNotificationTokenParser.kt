package co.subscription.domain.appleappstore

import co.subscription.domain.SubscriptionPurchaseData
import org.springframework.stereotype.Component

/**
 * App Store Server Notifications의 Webhook으로 들어오는 token을 parsing 합니다.
 */
@Component
class AppleAppStoreServerNotificationTokenParser {
    fun parse(jws: String): SubscriptionPurchaseData {
        val appleAppStoreServerNotification = AppleAppStoreServerNotification.from(jws)
        return appleAppStoreServerNotification.toSubscriptionPurchase()
    }
}