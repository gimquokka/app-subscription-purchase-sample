package co.subscription.client

import co.subscription.config.GooglePlaySecretsManager
import co.subscription.domain.googleplay.GooglePlayPurchaseChangeMessage
import co.subscription.domain.googleplay.GooglePlayPurchaseChangeSubscriberClient
import co.subscription.domain.googleplay.DeveloperNotification
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.api.gax.core.CredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub
import com.google.cloud.pubsub.v1.stub.SubscriberStub
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings
import com.google.pubsub.v1.AcknowledgeRequest
import com.google.pubsub.v1.PullRequest
import com.google.pubsub.v1.ReceivedMessage
import org.springframework.stereotype.Component

@Component
internal class GooglePlayPurchaseChangeSubscriberClientImpl(
    private val googlePlaySecretsManager: GooglePlaySecretsManager,
    private val objectMapper: ObjectMapper,
) : GooglePlayPurchaseChangeSubscriberClient {
    override fun pull(numOfMessages: Int): Pair<List<String>, List<GooglePlayPurchaseChangeMessage>> {
        val (ackIds, data) = pullMessages().unzip()

        val allNotifications =
            data
                .map { objectMapper.readValue<DeveloperNotification>(it) }
                .filter { it.testNotification == null }
        val messages = allNotifications.map { it.toGooglePlayPurchaseChangeMessage() }

        return ackIds to messages
    }

    private fun pullMessages(numOfMessages: Int = 20): List<Pair<String, String>> {
        val subscriberStub = createSubscriberStub()

        val subscriptionName = googlePlaySecretsManager.billingSub
        val pullRequest =
            PullRequest
                .newBuilder()
                .setMaxMessages(numOfMessages)
                .setSubscription(subscriptionName)
                .build()

        val messages: MutableList<ReceivedMessage> =
            subscriberStub.pullCallable().call(pullRequest).receivedMessagesList

        subscriberStub.shutdown()

        return messages.map { it.ackId to it.message.data.toStringUtf8() }
    }

    private fun createSubscriberStub(): SubscriberStub {
        val credentials = GoogleCredentials.fromStream(googlePlaySecretsManager.serviceAccount.byteInputStream())
        val credentialsProvider = CredentialsProvider { credentials }
        val transportChannelProvider =
            SubscriberStubSettings
                .defaultGrpcTransportProviderBuilder()
                .setMaxInboundMessageSize(20 * 1024 * 1024) // 20MB (maximum message size).
                .build()
        val subscriberStubSettings =
            SubscriberStubSettings
                .newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .setTransportChannelProvider(transportChannelProvider)
                .build()

        return GrpcSubscriberStub.create(subscriberStubSettings)
    }

    override fun acknowledge(ackIds: List<String>) {
        if (ackIds.isEmpty()) return
        val subscriberStub = createSubscriberStub()

        val acknowledgeRequest =
            AcknowledgeRequest
                .newBuilder()
                .setSubscription(googlePlaySecretsManager.billingSub)
                .addAllAckIds(ackIds)
                .build()
        subscriberStub.acknowledgeCallable().call(acknowledgeRequest)
        subscriberStub.shutdown()
    }
}