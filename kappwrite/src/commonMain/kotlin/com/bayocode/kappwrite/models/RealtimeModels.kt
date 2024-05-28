package com.bayocode.kappwrite.models

import io.ktor.utils.io.core.Closeable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlin.collections.Collection

@Serializable
data class RealtimeSubscription(
    private val close: () -> Unit
) : Closeable {
    override fun close() = close.invoke()
}

@Serializable
data class RealtimeCallback(
    val channels: Collection<String>,
    val callback: (RealtimeResponseEvent<JsonElement>) -> Unit
)

@Serializable
open class RealtimeResponse(
    val type: String,
    val data: JsonElement
)

@Serializable
data class RealtimeResponseEvent<T>(
    val events: Collection<String>,
    val channels: Collection<String>,
    val timestamp: String,
    var payload: T
)

@Serializable
enum class RealtimeCode(val value: Int) {
    POLICY_VIOLATION(1008),
    UNKNOWN_ERROR(-1)
}