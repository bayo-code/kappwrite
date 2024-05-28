@file:OptIn(InternalSerializationApi::class)

package com.bayocode.kappwrite

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer

import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any") {
        element<String>("type")
        element<JsonElement>("value")
    }

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This class can be saved only by Json")
        val jsonElement = when (value) {
            is Int -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is List<*> -> JsonArray(value.map { Json.encodeToJsonElement(AnySerializer, it ?: JsonNull) })
            is Map<*, *> -> JsonObject(value.mapKeys { it.key.toString() }
                .mapValues { Json.encodeToJsonElement(AnySerializer, it.value ?: JsonNull) })
            else -> Json.encodeToJsonElement(value::class.serializer() as KSerializer<Any>, value)
        }
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This class can be loaded only by Json")
        val jsonElement = jsonDecoder.decodeJsonElement()
        return parseJsonElement(jsonElement)
    }

    private fun parseJsonElement(jsonElement: JsonElement): Any {
        return when (jsonElement) {
            is JsonPrimitive -> {
                when {
                    jsonElement.isString -> jsonElement.content
                    jsonElement.booleanOrNull != null -> jsonElement.boolean
                    jsonElement.longOrNull != null -> jsonElement.long
                    jsonElement.doubleOrNull != null -> jsonElement.double
                    else -> throw SerializationException("Unsupported primitive type: $jsonElement")
                }
            }
            is JsonArray -> jsonElement.map { parseJsonElement(it) }
            is JsonObject -> jsonElement.mapValues { parseJsonElement(it.value) }
            else -> throw SerializationException("Unsupported JSON element: $jsonElement")
        }
    }
}

@OptIn(InternalSerializationApi::class)
fun Any?.toJsonElement(): JsonElement {
    return when (this) {
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Array<*> -> this.toJsonArray()
        is List<*> -> this.toJsonArray()
        is Map<*, *> -> this.toJsonObject()
        is JsonElement -> this
        else -> this?.let {
            val serializer: KSerializer<Any> = this::class.serializer() as KSerializer<Any>
            Json.encodeToJsonElement(serializer, this)
        } ?: JsonNull
    }
}

fun Array<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    this.forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

fun List<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    this.forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

fun Map<*, *>.toJsonObject(): JsonObject {
    val map = mutableMapOf<String, JsonElement>()
    this.forEach {
        if (it.key is String) {
            map[it.key as String] = it.value.toJsonElement()
        }
    }
    return JsonObject(map)
}

fun JsonObject.toMap(): Map<String, Any?> {
    return mapValues { it.value.toPrimitive() }
}

fun JsonArray.toList(): List<Any?> {
    return map { it.toPrimitive() }
}

fun JsonPrimitive.toLiteral(): Any? {
    return if (isString) toString()
    else booleanOrNull ?: intOrNull ?: longOrNull ?: floatOrNull ?: doubleOrNull
}

fun JsonElement.toPrimitive(): Any? {
    return when (this) {
        is JsonArray -> toList()
        is JsonObject -> toMap()
        is JsonPrimitive -> toLiteral()
        JsonNull -> null
    }
}