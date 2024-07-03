package co.subscription.application

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping

@Profile("!local")
@Component
class PurchaseDataUpdateScheduler(
    private val purchaseService: PurchaseService,
) {
    @Scheduled(cron = "*/60 * * * * *")
    @SchedulerLock(
        name = "update purchase change",
        lockAtLeastFor = "59S",
        lockAtMostFor = "59S",
    )
    fun updateGooglePlayPurchaseChange() {
        purchaseService.updateGooglePlayPurchaseChange()
    }

    @Scheduled(cron = "*/60 * * * * *")
    @SchedulerLock(
        name = "update purchase change",
        lockAtLeastFor = "59S",
        lockAtMostFor = "59S",
    )
    @PostMapping("/{your-app-store-webhook-url}")
    fun updateAppStoryPurchaseChange(someCallbackJws: String) {
        purchaseService.updateAppStorePurchaseChange(someCallbackJws)
    }
}