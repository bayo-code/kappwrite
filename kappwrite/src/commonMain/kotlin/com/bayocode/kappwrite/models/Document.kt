@file:UseSerializers(AnySerializer::class)
package com.bayocode.kappwrite.models

import com.bayocode.kappwrite.AnySerializer
import com.bayocode.kappwrite.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


/**
 * Document
 */
@Serializable(with = DocumentSerializer::class)
data class Document(
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
    val permissions: List<JsonElement>,

    /**
     * Additional properties
     */
    val _data: JsonElement?
) {

    inline fun<reified T> data(): T? {
        return _data?.let { json.decodeFromJsonElement(_data) }
    }

    @OptIn(InternalSerializationApi::class)
    fun toMap(): Map<String, Any> = mapOf(
        "\$id" to id as Any,
        "\$collectionId" to collectionId as Any,
        "\$databaseId" to databaseId as Any,
        "\$createdAt" to createdAt as Any,
        "\$updatedAt" to updatedAt as Any,
        "\$permissions" to permissions as Any,
        "data" to (_data ?: JsonNull)
    )
}

@OptIn(ExperimentalSerializationApi::class)
object DocumentSerializer : KSerializer<Document> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Document") {
        element<String>("\$id")
        element<String>("\$collectionId")
        element<String>("\$databaseId")
        element<String>("\$createdAt")
        element<String>("\$updatedAt")
        element("\$permissions", listSerialDescriptor<JsonElement>())
    }

    override fun serialize(encoder: Encoder, value: Document) {
        val compositeEncoder = encoder.beginStructure(descriptor)
        compositeEncoder.encodeStringElement(descriptor, 0, value.id)
        compositeEncoder.encodeStringElement(descriptor, 1, value.collectionId)
        compositeEncoder.encodeStringElement(descriptor, 2, value.databaseId)
        compositeEncoder.encodeStringElement(descriptor, 3, value.createdAt)
        compositeEncoder.encodeStringElement(descriptor, 4, value.updatedAt)
        compositeEncoder.encodeSerializableElement(descriptor, 5, ListSerializer(JsonObject.serializer()), value.permissions.map { it as JsonObject })
        compositeEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Document {
        val element = (decoder as JsonDecoder).decodeJsonElement()

        val id: String? = element.jsonObject["\$id"]?.jsonPrimitive?.contentOrNull
        val collectionId: String? = element.jsonObject["\$collectionId"]?.jsonPrimitive?.contentOrNull
        val databaseId: String? = element.jsonObject["\$databaseId"]?.jsonPrimitive?.contentOrNull
        val createdAt: String? = element.jsonObject["\$createdAt"]?.jsonPrimitive?.contentOrNull
        val updatedAt: String? = element.jsonObject["\$updatedAt"]?.jsonPrimitive?.contentOrNull
        val permissions: List<JsonElement>? = element.jsonObject["\$permissions"]?.jsonArray

        return Document(
            id = id ?: throw SerializationException("Missing id"),
            collectionId = collectionId ?: throw SerializationException("Missing collectionId"),
            databaseId = databaseId ?: throw SerializationException("Missing databaseId"),
            createdAt = createdAt ?: throw SerializationException("Missing createdAt"),
            updatedAt = updatedAt ?: throw SerializationException("Missing updatedAt"),
            permissions = permissions ?: throw SerializationException("Missing permissions"),
            _data = element
        )
    }
}