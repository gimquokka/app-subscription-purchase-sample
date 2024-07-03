package co.subscription.storage.base

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime

@MappedSuperclass
abstract class BaseTimeEntity {
    // todo: createAt, updateAt requried로 변경
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null

    @UpdateTimestamp
    var updatedAt: ZonedDateTime? = null
        protected set
}