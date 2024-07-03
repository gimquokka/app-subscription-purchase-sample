package co.subscription.config

import org.springframework.context.annotation.Configuration

@Configuration
class GooglePlayAndroidPubliserConfig(
    private val googlePlaySecretsManager: GooglePlaySecretsManager,
) {
    // TODO: 실제 프로젝트에서 사용할 때 주석 해제
//    @Bean
//    fun androidPublisher(): AndroidPublisher? {
//        val credential =
//            GoogleCredential
//                .fromStream(googlePlaySecretsManager.serviceAccount.byteInputStream())
//                .createScoped(listOf(AndroidPublisherScopes.ANDROIDPUBLISHER))
//
//        return AndroidPublisher
//            .Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JacksonFactory.getDefaultInstance(),
//                credential,
//            ).setApplicationName(googlePlaySecretsManager.applicationPackageName)
//            .build()
//    }
}