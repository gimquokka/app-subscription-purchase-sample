package co.subscription

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SubscriptionSampleApplication

fun main(args: Array<String>) {
    runApplication<SubscriptionSampleApplication>(*args)
}