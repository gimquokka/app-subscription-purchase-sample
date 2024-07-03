package co.subscription.client

import co.subscription.config.AppStoreSecretsManager
import co.subscription.domain.Purchase
import co.subscription.domain.SubscriptionPurchaseData
import co.subscription.domain.appleappstore.AppleAppStorePurchaseClient
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
internal class AppleAppStorePurchaseClientImpl(
    private val appStoreSecretsManager: AppStoreSecretsManager,
) : AppleAppStorePurchaseClient {
    private val log = KotlinLogging.logger {}

    override fun findSubscriptionPurchase(
        productId: String,
        token: String,
    ): SubscriptionPurchaseData? {
        val requestBody = """
                    {
                        "receipt-data": "$token",
                        "password": "${appStoreSecretsManager.SHARED_SECRET}"
                    }
                """

        val (_, _, result) =
            "https://buy.itunes.apple.com/verifyReceipt"
                .httpPost()
                .body(requestBody)
                .responseObject<AppStoreConnectVerificationResponse>()

        if (result is Result.Failure) {
            log.warn { result }
            throw IllegalStateException("POST https://buy.itunes.apple.com/verifyReceipt API 호출에 실패하였습니다.")
        }

        var responseBody = result.get()
        if (responseBody.isSandBoxEnvironment) {
            responseBody =
                "https://sandbox.itunes.apple.com/verifyReceipt"
                    .httpPost()
                    .body(requestBody)
                    .responseObject<AppStoreConnectVerificationResponse>()
                    .third
                    .get()
        }

        if (responseBody.isValid) {
            return responseBody.toSubscriptionPurchase()
        }

        return null
    }

    /**
     * 단건 결제에 대한 내용은 구현하지 않았습니다.
     */
    override fun findNonSubscriptionPurchase(
        productId: String,
        token: String,
    ): Purchase? {
        TODO("Not yet implemented")
    }
}