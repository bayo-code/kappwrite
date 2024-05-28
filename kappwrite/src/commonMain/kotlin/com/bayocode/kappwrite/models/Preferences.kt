package com.bayocode.kappwrite.models

import com.bayocode.kappwrite.json
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


/**
 * Preferences
 */
@Serializable
data class Preferences<T>(
    /**
     * Additional properties
     */
    @SerialName("data")
    val data: T? = null
) {
    @OptIn(InternalSerializationApi::class)
    fun toMap(): Map<String, Any> = mapOf(
        "data" to let {
            val klass = data!!::class as KClass<Any>
            json.encodeToJsonElement(klass.serializer(), data).jsonObject.toMap()
        }
    )
}