package co.subscription.domain.appleappstore

/**
 * Apple App Store Connect API 요청 인증을 위한 JWT를 생성하는 클래스입니다.
 */
interface AppleAppStoreConnectApiJwtGenerator {
    fun generate(): String
}