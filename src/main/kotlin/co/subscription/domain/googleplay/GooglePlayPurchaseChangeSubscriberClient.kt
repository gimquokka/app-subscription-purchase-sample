package co.subscription.domain.googleplay

interface GooglePlayPurchaseChangeSubscriberClient {
    fun pull(numOfMessages: Int = 20): Pair<List<String>, List<GooglePlayPurchaseChangeMessage>>

    fun acknowledge(ackIds: List<String>)
}