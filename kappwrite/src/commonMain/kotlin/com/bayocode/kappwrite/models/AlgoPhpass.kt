package com.bayocode.kappwrite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * AlgoPHPass
 */
@Serializable
data class AlgoPhpass(
    /**
     * Algo type.
     */
    @SerialName("type")
    val type: String,

) {
    fun toMap(): Map<String, Any> = mapOf(
        "type" to type as Any,
    )

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun from(
            map: Map<String, Any>,
        ) = AlgoPhpass(
            type = map["type"] as String,
        )
    }
}