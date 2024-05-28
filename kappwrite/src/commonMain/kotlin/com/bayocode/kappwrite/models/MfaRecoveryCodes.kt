package com.bayocode.kappwrite.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


/**
 * MFA Recovery Codes
 */
@Serializable
data class MfaRecoveryCodes(
    /**
     * Recovery codes.
     */
    @SerialName("recoveryCodes")
    val recoveryCodes: List<JsonElement>,

    ) {
    fun toMap(): Map<String, Any> = mapOf(
        "recoveryCodes" to recoveryCodes as Any,
    )

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun from(
            map: Map<String, Any>,
        ) = MfaRecoveryCodes(
            recoveryCodes = map["recoveryCodes"] as List<JsonElement>,
        )
    }
}