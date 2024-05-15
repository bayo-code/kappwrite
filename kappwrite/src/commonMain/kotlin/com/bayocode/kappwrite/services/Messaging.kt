package com.bayocode.kappwrite.services

import com.bayocode.kappwrite.Client
import com.bayocode.kappwrite.HttpMethod
import com.bayocode.kappwrite.ResponseType
import com.bayocode.kappwrite.Service

import com.bayocode.kappwrite.models.*

/**
 * The Messaging service allows you to send messages to any provider type (SMTP, push notification, SMS, etc.).
**/
class Messaging(client: Client) : Service(client) {

    /**
     * Create subscriber
     *
     * Create a new subscriber.
     *
     * @param topicId Topic ID. The topic ID to subscribe to.
     * @param subscriberId Subscriber ID. Choose a custom Subscriber ID or a new Subscriber ID.
     * @param targetId Target ID. The target ID to link to the specified Topic ID.
     * @return [Subscriber]
     */
    suspend fun createSubscriber(
        topicId: String,
        subscriberId: String,
        targetId: String,
    ): Subscriber {
        val apiPath = "/messaging/topics/{topicId}/subscribers"
            .replace("{topicId}", topicId)

        val apiParams = mutableMapOf<String, Any?>(
            "subscriberId" to subscriberId,
            "targetId" to targetId,
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Post,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


    /**
     * Delete subscriber
     *
     * Delete a subscriber by its unique ID.
     *
     * @param topicId Topic ID. The topic ID subscribed to.
     * @param subscriberId Subscriber ID.
     * @return [Any]
     */
    suspend fun deleteSubscriber(
        topicId: String,
        subscriberId: String,
    ): Any {
        val apiPath = "/messaging/topics/{topicId}/subscribers/{subscriberId}"
            .replace("{topicId}", topicId)
            .replace("{subscriberId}", subscriberId)

        val apiParams = mutableMapOf<String, Any?>(
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Delete,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


}