package co.subscription.storage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
internal interface InAppPurchaseJpaRepository : JpaRepository<InAppPurchaseEntity, Long> {
    fun findByOrderId(orderId: String): InAppPurchaseEntity?

    @Query(
        """
        SELECT *
        FROM in_app_purchase
        WHERE device_uuid = :deviceUuid
          AND product_id = :productId
          AND subscription_expiry_at > NOW()
        ORDER BY updated_at DESC
        LIMIT 1
    """,
        nativeQuery = true,
    )
    fun findLatestActive(deviceUuid: String, productId: String): InAppPurchaseEntity?
}