package co.subscription.config

import org.springframework.context.annotation.Configuration

@Configuration
class AppStoreSecretsManager {
    /** TODO: 비밀키를 아래와 같이 설정할 수 있습니다.
     * @Value("\${apple-app-store-connect.shared-secret}")
     * lateinit var SHARED_SECRET: String
     */
    var SHARED_SECRET: String = "your-shared-secret"
}