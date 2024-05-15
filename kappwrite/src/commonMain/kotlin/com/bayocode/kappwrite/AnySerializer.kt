package com.bayocode.kappwrite

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer

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