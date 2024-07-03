package co.subscription.config

import org.springframework.context.annotation.Configuration

@Configuration
class AppleAppStoreConnectSecretManager {
    /** TODO: 비밀키를 아래와 같이 설정할 수 있습니다.
     * @Value("\${apple-app-store-connect.issuer-id}")
     * lateinit var issuerId: String
     *
     * @Value("\${apple-app-store-connect.key-id}")
     * lateinit var keyId: String
     *
     * @Value("\${apple-app-store-connect.private-key}")
     * lateinit var privateKey: String
     */

    var issuerId: String = "your-issuer-id"
    var keyId: String = "your-key-id"
    var privateKey: String = "your-private-key"
}