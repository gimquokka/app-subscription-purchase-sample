package co.subscription.config

import org.springframework.context.annotation.Configuration

@Configuration
class GooglePlaySecretsManager {
    /** TODO: 비밀키를 아래와 같이 설정할 수 있습니다.
     * @Value("\${google-play-service-account.json}")
     * lateinit var serviceAccount: String
     *
     * @Value("\${google-play-application-package-name}")
     * lateinit var applicationPackageName: String
     *
     * @Value("\${google_play_billing-sub}")
     * lateinit var billingSub: String
     */

    var serviceAccount: String = "your-service-account"
    var applicationPackageName: String = "your-application-package-name"
    var billingSub: String = "your-billing-sub"
}