package com.bayocode.kappwrite.models

import com.bayocode.kappwrite.json
import kotlinx.serialization.Contextual
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


/**
 * Document
 */
@Serializable
data class Document<T>(
    /**
     * Document ID.
     */
    @SerialName("\$id")
    val id: String,

    /**
     * Collection ID.
     */
    @SerialName("\$collectionId")
    val collectionId: String,

    /**
     * Database ID.
     */
    @SerialName("\$databaseId")
    val databaseId: String,

    /**
     * Document creation date in ISO 8601 format.
     */
    @SerialName("\$createdAt")
    val createdAt: String,

    /**
     * Document update date in ISO 8601 format.
     */
    @SerialName("\$updatedAt")
    val updatedAt: String,

    /**
     * Document permissions. [Learn more about permissions](https://appwrite.io/docs/permissions).
     */
    @SerialName("\$permissions")
    val permissions: List<@Contextual Any>,

    /**
     * Additional properties
     */
    @SerialName("data")
    val data: T
) {
    @OptIn(InternalSerializationApi::class)
    fun toMap(): Map<String, Any> = mapOf(
        "\$id" to id as Any,
        "\$collectionId" to collectionId as Any,
        "\$databaseId" to databaseId as Any,
        "\$createdAt" to createdAt as Any,
        "\$updatedAt" to updatedAt as Any,
        "\$permissions" to permissions as Any,
        "data" to let {
            val klass = data!!::class as KClass<Any>
            json.encodeToJsonElement(klass.serializer(), data).jsonObject.toMap()
        }
    )
}