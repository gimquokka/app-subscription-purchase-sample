package co.subscription.domain.appleappstore

import co.subscription.config.AppleAppStoreConnectSecretManager
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Date

@Component
class AppleAppStoreConnectApiJwtGeneratorImpl(
    private val appleAppStoreConnectSecretManager: AppleAppStoreConnectSecretManager,
) : AppleAppStoreConnectApiJwtGenerator {
    override fun generate(): String {
        val header =
            hashMapOf<String, Any>(
                "alg" to SignatureAlgorithm.ES256,
//            "kid" to appleAppStoreConnectSecretManager.keyId,
                "kid" to "8R7749S3V7",
                "typ" to "JWT",
            )

        val issuedAt = Date()

        // 주의: 일반적으로 만료시간이 20분보다 크면 애플에서 거부함.
        // cf) https://developer.apple.com/documentation/appstoreconnectapi/generating_tokens_for_api_requests#3878467
        val expiredAt = Date(Date().time + 20 * 60 * 1000)

        val encodedKey = Base64.decodeBase64(appleAppStoreConnectSecretManager.privateKey)
        val keySpec = PKCS8EncodedKeySpec(encodedKey)
        val privateKey = KeyFactory.getInstance("EC").generatePrivate(keySpec)

        return Jwts
            .builder()
            .setHeader(header)
            .setIssuer(appleAppStoreConnectSecretManager.issuerId)
            .setIssuedAt(issuedAt)
            .setExpiration(expiredAt)
            .setAudience("appstoreconnect-v1") // 애플 API정의서
            .claim("bid", "your-bundle-id")
            .signWith(SignatureAlgorithm.ES256, privateKey)
            .compact()
    }
}